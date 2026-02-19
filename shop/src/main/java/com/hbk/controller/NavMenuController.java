package com.hbk.controller;

import com.hbk.dto.NavMenuResponseDTO;
import com.hbk.service.NavMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/nav-menus")
public class NavMenuController {

    private final NavMenuService navMenuService;

    @GetMapping("/tree")
    public ResponseEntity<List<NavMenuResponseDTO>> tree(){
        return ResponseEntity.ok(navMenuService.tree());
    }

    @PostMapping
    public ResponseEntity<NavMenuResponseDTO> create(@RequestBody NavMenuResponseDTO req){
        return ResponseEntity.ok(navMenuService.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        navMenuService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
