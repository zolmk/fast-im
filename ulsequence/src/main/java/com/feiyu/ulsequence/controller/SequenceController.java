package com.feiyu.ulsequence.controller;

import com.feiyu.ulsequence.Result;
import com.feiyu.ulsequence.Sequence;
import com.feiyu.ulsequence.service.ISequenceService;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhufeifei 2024/6/5
 **/

@RestController
@RequestMapping("/seq")
public class SequenceController {
    private final ISequenceService sequenceService;
    public SequenceController(ISequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }
    @GetMapping("/genseq/{uid}")
    @ResponseBody
    public Result<?> gen(@PathVariable("uid") Long uid) {
        return this.sequenceService.gen(uid);
    }
}
