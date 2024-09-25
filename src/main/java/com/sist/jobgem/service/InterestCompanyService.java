package com.sist.jobgem.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.sist.jobgem.dto.InterestCompanyDto;
import com.sist.jobgem.entity.InterestCompany;
import com.sist.jobgem.mapper.InterestCompanyMapper;
import com.sist.jobgem.repository.InterestCompanyRepository;

@Service
public class InterestCompanyService {
    @Autowired
    private InterestCompanyRepository interestCompanyRepository;

    public String managementInterest(Integer coIdx, Integer joIdx) {
        InterestCompany interestCompany = interestCompanyRepository.findByCoIdxAndJoIdx(coIdx, joIdx);
        if(interestCompany == null){
            InterestCompanyDto interestCompanyDto = new InterestCompanyDto();
            interestCompanyDto.setCoIdx(coIdx);
            interestCompanyDto.setJoIdx(joIdx);
            interestCompanyDto.setIcDate(LocalDate.now());
            interestCompanyRepository.save(InterestCompanyMapper.INSTANCE.toEntity(interestCompanyDto));
            return "add";
        }
        interestCompanyRepository.delete(interestCompany);
        return "delete";
    }

    public boolean isInterest(Integer coIdx, Integer joIdx) {
        InterestCompany interestCompany = interestCompanyRepository.findByCoIdxAndJoIdx(coIdx, joIdx);
        if(interestCompany == null){
            return false;
        }
        return true;
    }
}