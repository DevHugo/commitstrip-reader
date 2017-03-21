package com.commitstrip.commitstripreader.backend.controller;

import com.commitstrip.commitstripreader.backend.service.StripService;
import com.commitstrip.commitstripreader.dto.StripDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class StripController {

    @Autowired
    private StripService stripService;

    @RequestMapping(value = "/strip/", method = RequestMethod.GET, produces = {"application/json"})
    public Page<StripDto> findAll(Pageable pageable) {
        return stripService.findAll(pageable);
    }

    @RequestMapping(value = "/strip/{idStrip:\\d+}", method = RequestMethod.GET, produces = {
            "application/json"})
    public ResponseEntity<StripDto> findOne(@PathVariable("idStrip") Long idStrip) {

        StripDto strip = stripService.findOne(idStrip);

        if (strip != null) {
            return new ResponseEntity(strip, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/strip/recent", method = RequestMethod.GET, produces = {
            "application/json"})
    public StripDto recent() {
        return stripService.findMoreRecent();
    }
}
