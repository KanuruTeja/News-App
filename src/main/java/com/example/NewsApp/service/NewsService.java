package com.example.NewsApp.service;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.NewsApp.dto.NewsBasicDto;
import com.example.NewsApp.dto.NewsCommentDto;
import com.example.NewsApp.dto.NewsDetailResponse;
import com.example.NewsApp.dto.NewsPendingDto;
import com.example.NewsApp.dto.NewsPublishedDto;
import com.example.NewsApp.dto.NewsRejectedDto;
import com.example.NewsApp.dto.NewsView;
import com.example.NewsApp.dto.ReporterProfileResponse;
import com.example.NewsApp.entity.News;
import com.example.NewsApp.entity.NewsComment;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.*;
import com.example.NewsApp.entity.UserRole;
import com.example.NewsApp.enums.DateFilter;
import com.example.NewsApp.enums.NewsStatus;
import com.example.NewsApp.exception.ApiException;
import com.example.NewsApp.repository.LocationRepository;
import com.example.NewsApp.repository.NewsCommentRepository;
import com.example.NewsApp.repository.NewsLikeRepository;
import com.example.NewsApp.repository.NewsRepository;
import com.example.NewsApp.repository.NewsShareRepository;
import com.example.NewsApp.repository.ReporterRepository;
import com.example.NewsApp.repository.UserRepository;
import com.example.NewsApp.repository.UserRoleRepository;

import jakarta.transaction.Transactional;

@Service
public class NewsService {


    private static final Long ADMIN_ROLE_ID = 1L;
    private static final Long REPORTER_ROLE_ID = 2L;

    private static final int ADMIN_ROLE_IDs = 1;
    private static final int REPORTER_ROLE_IDs = 2;

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userrepo;
    

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private ReporterRepository reporterRepo;
    

    @Autowired
    private NewsShareRepository  newsShareRepo;
    @Autowired
    private NewsCommentRepository  newsCommentRepo;
    @Autowired
    private NewsLikeRepository  newsLikeRepo;
    @Autowired
    private LocationRepository  locationrepo;
    
    
    

    public List<NewsView> getNewsWithFiltersAndStats(
            Long userId,
            NewsStatus status,
            DateFilter dateFilter,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        UserRole userRole = userRoleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Long roleId = userRole.getRole().getId();

        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.now();

        if (dateFilter != null) {
            switch (dateFilter) {
                case TODAY -> start = LocalDate.now().atStartOfDay();
                case LAST_7_DAYS -> start = LocalDateTime.now().minusDays(7);
                case LAST_30_DAYS -> start = LocalDateTime.now().minusDays(30);
                case LAST_6_MONTHS -> start = LocalDateTime.now().minusMonths(6);
                case LAST_1_YEAR -> start = LocalDateTime.now().minusYears(1);
                case CUSTOM -> {
                    start = fromDate.atStartOfDay();
                    end = toDate.atTime(23, 59, 59);
                }
            }
        }

        List<News> newsList;

        // ADMIN
        if (roleId.equals(ADMIN_ROLE_ID)) {
            newsList = fetchAdminNews(status, start, end);
        }
        // REPORTER
        else if (roleId.equals(REPORTER_ROLE_ID)) {
            User user = userrepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            newsList = fetchReporterNews(user, status, start, end);
        }
        else {
            return List.of();
        }

        return newsList.stream()
                .map(this::mapByStatus)
                .toList();
    }

    private List<News> fetchAdminNews(
            NewsStatus status,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start != null) {
            return status != null
                    ? newsRepository.findByStatusAndCreatedAtBetween(status, start, end)
                    : newsRepository.findByCreatedAtBetween(start, end);
        }
        return status != null
                ? newsRepository.findByStatus(status)
                : newsRepository.findAll();
    }

    private List<News> fetchReporterNews(
            User user,
            NewsStatus status,
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start != null) {
            return status != null
                    ? newsRepository.findByCreatedByAndStatusAndCreatedAtBetween(
                            user, status, start, end)
                    : newsRepository.findByCreatedByAndCreatedAtBetween(
                            user, start, end);
        }
        return status != null
                ? newsRepository.findByCreatedByAndStatus(user, status)
                : newsRepository.findByCreatedBy(user);
    }

    private NewsView mapByStatus(News news) {

        long likes = newsLikeRepo.countByNewsId(news.getId());
        long comments = newsCommentRepo.countByNewsId(news.getId());
        long shares = newsShareRepo.countByNewsId(news.getId());
        long saves = shares; // or separate table later

        return switch (news.getStatus()) {

            case PENDING -> NewsPendingDto.builder()
                    .newsId(news.getId())
                    .headline(news.getHeadline())
                    .content(news.getContent())
                    .uploadedAt(news.getCreatedAt())
                    .reporterName(news.getCreatedBy().getName())
                    .reporterPhone(news.getCreatedBy().getMobileNumber())
                    .category(news.getCategory())
                    .build();

            case REJECTED -> NewsRejectedDto.builder()
                    .newsId(news.getId())
                    .headline(news.getHeadline())
                    .content(news.getContent())
                    .rejectionReason(news.getRejectionReason())
                    .category(news.getCategory())
                    .build();

            case PUBLISHED -> NewsPublishedDto.builder()
                    .newsId(news.getId())
                    .category(news.getCategory())
                    .headline(news.getHeadline())
                    .content(news.getContent())
                    .commentCount(comments)
                    .likeCount(likes)
                    .shareCount(shares)
                    .saveCount(saves)
                    .newsType(news.getNewsType())
                    .build();

            default -> NewsBasicDto.builder()
                    .newsId(news.getId())
                    .headline(news.getHeadline())
                    .content(news.getContent())
                    .uploadedAt(news.getCreatedAt())
                    .build();
        };
    }


    @Transactional
    public News uploadNews(News news, Long userId, String email) {

        // 1️⃣ User from JWT
        User loggedInUser = userrepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ SECURITY CHECK (VERY IMPORTANT)
        // User can upload ONLY for self unless ADMIN
        if (!loggedInUser.getId().equals(userId)) {

            UserRole loggedUserRole = userRoleRepository
                    .findByUserId(loggedInUser.getId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            if (!ADMIN_ROLE_ID.equals(loggedUserRole.getRole().getId())) {
                throw new RuntimeException("You are not allowed to upload news for another user");
            }
        }

        // 3️⃣ Get target user
        User targetUser = userrepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        // 4️⃣ Set mapping
        news.setUserId(userId);
        news.setCreatedBy(targetUser);

        // 5️⃣ Role-based status (based on uploader role)
        UserRole uploaderRole = userRoleRepository
                .findByUserId(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Long roleId = uploaderRole.getRole().getId();

        if (ADMIN_ROLE_ID.equals(roleId)) {
            news.setStatus(NewsStatus.PUBLISHED);
            news.setPublishedAt(LocalDateTime.now());
        } else if (REPORTER_ROLE_ID.equals(roleId)) {
            news.setStatus(NewsStatus.PENDING);
        } else {
            throw new RuntimeException("User not allowed to upload news");
        }

        return newsRepository.save(news);
    }



    //   ========== count for admin and repoter===========
    public Map<String, Long> getDashboardStats(
            Long userId,
            Integer roleId,
            boolean todayOnly
    ) {
        if(!userrepo.existsById(userId)){
            throw new RuntimeException(("Invalid userId: User not found"));
        }

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        Map<String, Long> stats = new LinkedHashMap<>();

        // ================= ADMIN =================
        if (roleId == ADMIN_ROLE_IDs) {

            stats.put("Total News",
                    todayOnly
                            ? newsRepository.countByCreatedAtBetween(start, end)
                            : newsRepository.count());

            stats.put("Pending News",
                    todayOnly
                            ? newsRepository.countByStatusAndCreatedAtBetween(
                            NewsStatus.PENDING, start, end)
                            : newsRepository.countByStatus(NewsStatus.PENDING));

            stats.put("Published News",
                    todayOnly
                            ? newsRepository.countByStatusAndCreatedAtBetween(
                            NewsStatus.PUBLISHED, start, end)
                            : newsRepository.countByStatus(NewsStatus.PUBLISHED));

            stats.put("Rejected News",
                    todayOnly
                            ? newsRepository.countByStatusAndCreatedAtBetween(
                            NewsStatus.REJECTED, start, end)
                            : newsRepository.countByStatus(NewsStatus.REJECTED));

            stats.put("Reporter Management",
                    todayOnly
                            ? reporterRepo.countByRegisteredAtBetween(start, end)
                            : reporterRepo.count());
        }

        // ================= REPORTER =================
        else if (roleId == REPORTER_ROLE_IDs) {

            stats.put("Total News",
                    todayOnly
                            ? newsRepository.countByCreatedBy_IdAndCreatedAtBetween(
                            userId, start, end)
                            : newsRepository.countByCreatedBy_Id(userId));

            stats.put("Pending News",
                    todayOnly
                            ? newsRepository.countByCreatedBy_IdAndStatusAndCreatedAtBetween(
                            userId, NewsStatus.PENDING, start, end)
                            : newsRepository.countByCreatedBy_IdAndStatus(
                            userId, NewsStatus.PENDING));

            stats.put("Published News",
                    todayOnly
                            ? newsRepository.countByCreatedBy_IdAndStatusAndCreatedAtBetween(
                            userId, NewsStatus.PUBLISHED, start, end)
                            : newsRepository.countByCreatedBy_IdAndStatus(
                            userId, NewsStatus.PUBLISHED));

            stats.put("Rejected News",
                    todayOnly
                            ? newsRepository.countByCreatedBy_IdAndStatusAndCreatedAtBetween(
                            userId, NewsStatus.REJECTED, start, end)
                            : newsRepository.countByCreatedBy_IdAndStatus(
                            userId, NewsStatus.REJECTED));
        }
        else {
            throw new RuntimeException("Invalid roleId");
        }

        return stats;
    }
    
    
    public NewsDetailResponse getNewsDetailsById(Long newsId) {

        // 1️⃣ Fetch news
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));

        User reporter = news.getCreatedBy();

        String reporterName = reporter != null ? reporter.getName() : null;
        String reporterPhone = reporter != null ? reporter.getMobileNumber() : null;
        String reporterEmail = reporter != null ? reporter.getEmail() : null;
        Long reporterId = reporter != null ? reporter.getId() : null;
        String city = reporter != null && reporter.getLocation() != null ? reporter.getLocation().getCity() : null;
        String state = reporter != null && reporter.getLocation() != null ? reporter.getLocation().getState() : null;

        // 2️⃣ Counts
        long likeCount = newsLikeRepo.countByNewsId(newsId);
        long commentCount = newsCommentRepo.countByNewsId(newsId);
        long shareCount = newsShareRepo.countByNewsId(newsId);
        long saveCount = shareCount; // adjust if separate save table

        // 3️⃣ Fetch all comments and preload users
        List<NewsComment> commentList = newsCommentRepo.findByNewsIdOrderByCreatedAtDesc(newsId);
        List<Long> userIds = commentList.stream()
                .map(NewsComment::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userrepo.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<NewsCommentDto> comments = commentList.stream()
                .map(c -> {
                    User commenter = userMap.get(c.getUserId());
                    String commenterName = commenter != null ? commenter.getName() : "User-" + c.getUserId();
                    return NewsCommentDto.builder()
                            .commentId(c.getId())
                            .comment(c.getComment())
                            .userId(c.getUserId())
                            .userName(commenterName)
                            .commentedAt(c.getCreatedAt())
                            .build();
                }).toList();

        // 4️⃣ Build response
        return NewsDetailResponse.builder()
                .newsId(news.getId())
                .headline(news.getHeadline())
                .content(news.getContent())
                .category(news.getCategory())
                .newsType(news.getNewsType())
                .mediaUrl(news.getMediaUrl())
                .uploadedAt(news.getCreatedAt())

                // Reporter
                .reporterId(reporterId)
                .reporterName(reporterName)
                .reporterEmail(reporterEmail)
                .reporterPhone(reporterPhone)

                // Location
                .district(news.getDistrict())
                .city(city)
                .state(state)

                // Counts
                .likeCount(likeCount)
                .commentCount(commentCount)
                .shareCount(shareCount)
                .saveCount(saveCount)

                // Comments
                .comments(comments)
                .build();
    }

  //==========Get Repoter By ID=================
    public ReporterProfileResponse getReporterProfile(Long userId, Long roleId) {


        if (!roleId.equals(ADMIN_ROLE_ID) && !roleId.equals(REPORTER_ROLE_ID)) {
            throw new ApiException(
                    "Invalid role. Only reporters and admins are allowed",
                    HttpStatus.BAD_REQUEST
            );
        }

        //  Validate reporter exists
        User user = userrepo.findById(userId)
                .orElseThrow(() ->
                        new ApiException("Reporter not found", HttpStatus.NOT_FOUND)
                );

        //  Validate target user IS reporter
        UserRole userRole = userRoleRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ApiException("User role not assigned", HttpStatus.BAD_REQUEST)
                );

        if (!userRole.getRole().getId().equals(REPORTER_ROLE_ID)) {
            throw new ApiException("User is not a reporter", HttpStatus.BAD_REQUEST);
        }

        Location location = locationrepo.findByUserId(userId).orElse(null);

        return ReporterProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .enabled(user.isEnabled())
                .city(location != null ? location.getCity() : null)
                .state(location != null ? location.getState() : null)
                .zipCode(location != null ? location.getZipCode() : null)
                .build();
    }

//  ===============get all repoters==============
public List<ReporterProfileResponse> getAllReporters(Long roleId) {

  // Only ADMIN can access
  if (!roleId.equals(ADMIN_ROLE_ID)) {
      throw new ApiException(
              "Access denied. Only admin can fetch reporters",
              HttpStatus.FORBIDDEN
      );
  }

  // Fetch all REPORTER roles
  List<UserRole> reporterRoles =
          userRoleRepository.findByIdRoleId(REPORTER_ROLE_ID);

  if (reporterRoles.isEmpty()) {
      throw new ApiException(
              "No reporters found",
              HttpStatus.NOT_FOUND
      );
  }

  // Build response
  return reporterRoles.stream().map(ur -> {
      User user = ur.getUser();
      Location location =
              locationrepo.findByUserId(user.getId()).orElse(null);

      return ReporterProfileResponse.builder()
              .userId(user.getId())
              .name(user.getName())
              .email(user.getEmail())
              .mobileNumber(user.getMobileNumber())
              .enabled(user.isEnabled())
              .city(location != null ? location.getCity() : null)
              .state(location != null ? location.getState() : null)
              .zipCode(location != null ? location.getZipCode() : null)
              .build();
  }).toList();
}

//    ===============get all repoters==============

}
