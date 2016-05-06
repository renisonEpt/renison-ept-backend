package com.renison.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.renison.model.Test;
import com.renison.repository.TestRepository;

@RestController
@RequestMapping("/tests")
public class TestController extends BaseController<Test> {

    private TestRepository testRepo;

    @Autowired
    public TestController(TestRepository testRepo) {
        super(testRepo);
        this.testRepo = testRepo;
    }

    public Test getActive() {
        List<Test> tests = this.testRepo.getActiveTests();
        //TODO handle empty collection
        return tests.iterator().next();
    }
}