package com.career.careerlink.admin.commonCode.mapper;

import com.career.careerlink.admin.commonCode.dto.CommonCodeDto;
import com.career.careerlink.admin.commonCode.dto.CommonCodeSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommonCodeMapper {
    List<CommonCodeDto> getCommonCodes(String groupCode);
    // 단일 파라미터 넘길시
    long parentCodesCount(CommonCodeSearchRequest req);
    // 여러 파라미터 넘길시
    List<CommonCodeDto> parentCodes(@Param("req") CommonCodeSearchRequest req,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);
    long childCodesCount(CommonCodeSearchRequest req);
    List<CommonCodeDto> childCodes(@Param("req") CommonCodeSearchRequest req,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    List<CommonCodeDto> allCodesByGroup(@Param("groupCode") String groupCode);

    // (옵션) 캐스케이딩 셀렉트용 심플 쿼리
    List<CommonCodeDto> parentsByGroup(@Param("groupCode") String groupCode);
    List<CommonCodeDto> childrenByParent(@Param("groupCode") String groupCode,
                                         @Param("parentCode") String parentCode);

    // delete
    void deleteChildren(@Param("list") List<CommonCodeDto> list);
    void deleteParents(@Param("list") List<CommonCodeDto> list);

    // insert (중복 무시)
    void insertParents(@Param("list") List<CommonCodeDto> list);
    void insertChildren(@Param("list") List<CommonCodeDto> list);

    // update
    void updateParents(CommonCodeDto dto);
    void updateChildren(CommonCodeDto dto);
}
