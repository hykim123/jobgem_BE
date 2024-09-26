package com.sist.jobgem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.sist.jobgem.service.CompanyService;
import com.sist.jobgem.service.JobseekerService;
import com.sist.jobgem.service.PostService;
import com.sist.jobgem.service.UserService;
import com.sist.jobgem.service.BlackListService;
import com.sist.jobgem.service.BlockService;
import com.sist.jobgem.service.BoardService;
import com.sist.jobgem.dto.BlackListDto;
import com.sist.jobgem.dto.BlockDto;
import com.sist.jobgem.dto.BoardDto;
import com.sist.jobgem.dto.CompanyDto;
import com.sist.jobgem.dto.JobseekerDto;
import com.sist.jobgem.dto.PostDto;
import com.sist.jobgem.dto.UserDto;


@RestController
@RequestMapping("api/admin")
public class AdminController {
    
    @Autowired
    UserService userService;
    @Autowired
    CompanyService companyService;
    @Autowired
    PostService postService;
    @Autowired
    BlockService blockService;
    @Autowired
    JobseekerService jobseekerService;
    @Autowired
    BoardService boardService;
    @Autowired
    BlackListService blackListService;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
    @GetMapping("/jobseekers")
    public ResponseEntity<Page<JobseekerDto>> getAllJobseekers(Pageable pageable, @RequestParam(required = false, name= "type") String type, @RequestParam(required = false, name= "value") String value) {
        return ResponseEntity.ok(jobseekerService.getJobseekerList(pageable, type, value));
    }
    
    @GetMapping("/companies")
    public ResponseEntity<Page<CompanyDto>> getAllCompanies(Pageable pageable, @RequestParam(required = false, name= "type") String type, @RequestParam(required = false, name= "value") String value) {
        return ResponseEntity.ok(companyService.findAllCompanies(pageable, type, value));
    }

    @GetMapping("/blocked-jobseekers")
    public ResponseEntity<Page<BlockDto>> getJobseekerBlocklist(Pageable pageable, @RequestParam(required = false, name= "type") String type, @RequestParam(required = false, name= "value") String value) {
        return ResponseEntity.ok(blockService.findAllJobseekerBlocks(pageable, type, value));
    }

    @GetMapping("/blocked-companies")
    public ResponseEntity<Page<BlockDto>> getCompanyBlocklist(Pageable pageable, @RequestParam(required = false, name= "type") String type, @RequestParam(required = false, name= "value") String value) {
        return ResponseEntity.ok(blockService.findAllCompanyBlocks(pageable, type, value));
    }

    @GetMapping("/unblocked-jobseekers")
    public ResponseEntity<List<JobseekerDto>> getUnblockedJobseeker(@RequestParam(required = false, name= "type") String type, @RequestParam(required = false, name= "value") String value) {
        return ResponseEntity.ok(jobseekerService.findUnblockedJobseeker(type, value));
    }

    @GetMapping("/unblocked-companies")
    public ResponseEntity<List<CompanyDto>> getUnblockedCompany(@RequestParam(required = false, name= "type") String type, @RequestParam(required = false, name= "value") String value) {
        return ResponseEntity.ok(companyService.findUnblockedCompany(type, value));
    }

    @PostMapping("/jobseeker-blocks")
    public void addjobseekerBlock(@RequestParam(required = true, name= "id") int id, @RequestParam(required = true ,name= "value") String value) {
        blockService.addjobseekerBlock(id, value);
    }

    @DeleteMapping("/jobseeker-blocks")
    public int deletejobseekerBlock(@RequestParam(value = "chkList", required = true) List<String> chkList) {
        for (int i = 0; i < chkList.size(); i++) {
            blockService.deletecomjobBlock(Integer.parseInt(chkList.get(i)));
        }
        return chkList.size();
    }

    @PostMapping("/company-blocks")
    public void addcompanyBlock(@RequestParam(required = true, name = "id") int id, @RequestParam(required = true, name = "value") String value) {
        blockService.addcompanyBlock(id, value);
    }
    
    @DeleteMapping("/company-blocks")
    public int deletecompanyBlock(@RequestParam(value = "chkList", required = true) List<String> chkList) {
        for (int i = 0; i < chkList.size(); i++) {
            blockService.deletecomjobBlock(Integer.parseInt(chkList.get(i)));
        }
        return chkList.size();
    }

    @GetMapping("unanswered-questions")
    public ResponseEntity<List<BoardDto>> getQnaList() {
        return ResponseEntity.ok(boardService.getQnaList());
    }

    @GetMapping("pending-blacklist")
    public ResponseEntity<List<BlackListDto>> getPendingBlackList() {
        return ResponseEntity.ok(blackListService.findPendingBlackList());
    }

}

