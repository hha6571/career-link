package com.career.careerlink.main.service;


import com.career.careerlink.main.dto.MainEmployersDtos;
import com.career.careerlink.main.dto.MainJobsDtos;

public interface MainService {
    // 메인화면 공고조회
    MainJobsDtos.MainJobsResponse getMainJobs(String userId);
    // 메인화면 기업조회
    MainEmployersDtos.MainEmployersResponse getMainEmployers();
}
