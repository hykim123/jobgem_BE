package com.sist.jobgem.service;

import com.sist.jobgem.dto.FitJobseekerDto;
import com.sist.jobgem.repository.ApplymentRepository;
import com.sist.jobgem.repository.HaveSkillRepository;
import com.sist.jobgem.repository.InterestCompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

import com.sist.jobgem.dto.JobseekerDto;
import com.sist.jobgem.dto.UserDto;
import com.sist.jobgem.entity.Jobseeker;
import com.sist.jobgem.entity.Skill;
import com.sist.jobgem.entity.User;
import com.sist.jobgem.mapper.JobseekerMapper;
import com.sist.jobgem.mapper.UserMapper;
import com.sist.jobgem.repository.JobseekerRepository;
import com.sist.jobgem.repository.OfferRepository;
import com.sist.jobgem.repository.ScrapRepository;
import com.sist.jobgem.repository.SkillRepository;
import com.sist.jobgem.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class JobseekerService {
    @Autowired
    private JobseekerRepository jobseekerRepository;

    @Autowired
    private HaveSkillRepository haveSkillRepository;

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    ApplymentRepository applymentRepository;

    @Autowired
    OfferRepository offerRepository;

    @Autowired
    SkillRepository skillRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InterestCompanyRepository interestCompanyRepository;

    public JobseekerDto getJobseeker(int id) {
        JobseekerDto jobseeker = null;

        if (jobseekerRepository.findById(id).isPresent()) {
            jobseeker = JobseekerMapper.INSTANCE.toDto(jobseekerRepository.findById(id).get());
            System.out.println(jobseeker);
        }
        return jobseeker;
    }

    public FitJobseekerDto fitJobseekerList(int id, Pageable pageable) {
        FitJobseekerDto fitJobseeker = FitJobseekerDto.builder()
                .fitJobseekers(jobseekerRepository.findByWithfitJobseeker(id, pageable))
                .build();
        return fitJobseeker;
    }

    public Page<JobseekerDto> getJobseekerList(Pageable pageable, String value, String type) {
        if (value == null && type == null) {
            return jobseekerRepository.findAll(pageable).map(JobseekerMapper.INSTANCE::toDto);
        }
        return jobseekerRepository.findByTypeAndValueContaining(type, value, pageable)
                .map(JobseekerMapper.INSTANCE::toDto);
    }

    @Transactional
    public Jobseeker updateJobseekerDetails(int id, JobseekerDto jobseekerDto) {
        // Jobseeker 존재 여부 확인
        Jobseeker existingJobseeker = jobseekerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jobseeker not found"));

        // 스킬 업데이트
        List<Skill> selectedSkills = new ArrayList<>();
        if (jobseekerDto.getSkillIds() != null && !jobseekerDto.getSkillIds().isEmpty()) {
            selectedSkills = skillRepository.findAllById(jobseekerDto.getSkillIds());
        }

        // 기존 스킬 목록을 지우고 새 스킬 목록으로 대체
        existingJobseeker.getSkills().clear();
        existingJobseeker.getSkills().addAll(selectedSkills);

        // 기본 필드 업데이트 (set 없이 필드 직접 수정)
        existingJobseeker.updateFields(
                jobseekerDto.getJoName(),
                jobseekerDto.getJoBirth(),
                jobseekerDto.getJoAddress(),
                jobseekerDto.getJoTel(),
                jobseekerDto.getJoGender(),
                jobseekerDto.getJoEdu(),
                jobseekerDto.getJoSal(),
                jobseekerDto.getJoImgUrl());

        // 변경된 엔티티 저장
        return jobseekerRepository.save(existingJobseeker);
    }

    // 비밀번호 체크 함수
    public boolean checkPwd(int id, String chkPw) {
        // DB에서 유저 정보 가져오기 (usId로 가져온다고 가정)
        Jobseeker jobseeker = jobseekerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. ID: " + id));

        String usPw = jobseeker.getUser().getUsPw(); // DB에 저장된 비밀번호

        // 비밀번호가 해시로 저장된 경우 해시 비교 (예: BCrypt 사용)
        return usPw.equals(chkPw);
        // return passwordEncoder.matches(chkPw, usPw);
    }

    // 비밀번호 업데이트 서비스
    public boolean updatePassword(int id, String newPwd) {
        // Jobseeker를 ID로 조회
        Jobseeker jobseeker = jobseekerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + id));
        // Jobseeker와 연관된 User 객체 가져오기
        User user = jobseeker.getUser();
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자의 유저 정보가 없습니다.");
        }

        UserDto uDto = UserMapper.INSTANCE.toDto(user);

        // String encodedPassword = passwordEncoder.encode(newPwd);
        uDto.setUsPw(newPwd); // 새로운 비밀번호 설정

        // DTO를 다시 엔티티로 변환
        User updatedUser = UserMapper.INSTANCE.toEntity(uDto);

        userRepository.save(updatedUser); // User 엔티티를 저장
        return true; // 성공적으로 비밀번호 변경
    }

    // 회원 탈퇴
    public boolean deleteUser(int id) {
        // Jobseeker를 ID로 조회
        Jobseeker jobseeker = jobseekerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + id));
        // Jobseeker와 연관된 User 객체 가져오기
        User user = jobseeker.getUser();
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자의 유저 정보가 없습니다.");
        }

        UserDto uDto = UserMapper.INSTANCE.toDto(user);

        uDto.setUsState(0);

        User updatedUser = UserMapper.INSTANCE.toEntity(uDto);

        userRepository.save(updatedUser);
        return true;
    }

    public List<JobseekerDto> findUnblockedJobseeker(String value, String type) {
        if (value == null && type == null) {
            return JobseekerMapper.INSTANCE.toDtoList(jobseekerRepository.findAllJobseekersNotInBlock());
        } else {
            return JobseekerMapper.INSTANCE.toDtoList(jobseekerRepository.findJobseekersNotInBlock(value, type));
        }
    }

    public Map<String, Object> getMypageCount(int id) {
        Map<String, Object> map = new HashMap<>();

        map.put("지원완료", applymentRepository.countByJoIdx(id));
        map.put("이력서열람", applymentRepository.countByJoIdxAndApReadIsOne(id));
        map.put("입사제안", "고민");
        map.put("스크랩공고", scrapRepository.countByJoIdxAndScStateIsOne(id));
        map.put("관심기업공고", interestCompanyRepository.countByJoIdx(id));

        return map;
    }

}
