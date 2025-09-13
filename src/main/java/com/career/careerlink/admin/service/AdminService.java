package com.career.careerlink.admin.service;

import com.career.careerlink.admin.dto.*;
import com.career.careerlink.admin.entity.enums.Granularity;
import com.career.careerlink.faq.dto.FaqDto;
import com.career.careerlink.notice.dto.NoticeDetailDto;
import com.career.careerlink.notice.dto.NoticeDto;
import com.career.careerlink.notice.dto.NoticeRequestDto;
import com.career.careerlink.faq.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {
    List<AdminEmployerRequestDto> getAllEmployersWithFilter(AdminEmployerRequestDto searchRequest);
    void approveEmployer(String employerId);
    List<MenuDto> getAllMenus(String accessRole);
    Page<CommonCodeDto> getParentCodes(CommonCodeSearchRequest req);
    Page<CommonCodeDto> getChildCodes(CommonCodeSearchRequest req);
    void saveMenus(MenuDto saveDto);
    void saveCommonCodes(CommonCodeSaveDto saveDto);
    Page<UsersDto> getUsers(UsersRequestDto req);
    void saveUsers(@RequestBody List<UsersDto> list);
    Page<NoticeDto> getNotices(NoticeRequestDto req);
    Long updateNotice(NoticeDetailDto dto, MultipartFile file);
    Long createNotice(NoticeDetailDto dto, MultipartFile file);
    void deleteNotice(Long id);
    List<FaqDto> getFaqs(Category category);
    void createFaq(FaqDto dto);
    void updateFaq(FaqDto dto);
    void deleteFaq(Long faqId);
    List<PointDto> getPostingStats(Granularity granularity, LocalDate from, LocalDate to);
}
