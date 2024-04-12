package com.project.compagnoserver.controller;


import com.project.compagnoserver.domain.Animal.*;
import com.project.compagnoserver.domain.Animal.AnimalBoard;
import com.project.compagnoserver.domain.Animal.AnimalBoardDTO;
import com.project.compagnoserver.service.AnimalBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/compagno/*")
public class AnimalBoardController {

    @Autowired
    private AnimalBoardService animalBoardService;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    // 자유게시판 글쓰기
    @PostMapping("/animal-board")
    public ResponseEntity<AnimalBoard> write(AnimalBoardDTO dto) throws IOException {
        // 글작성
        AnimalBoard vo = new AnimalBoard(); // 추가로 필요한것 : userId/ animalCateCode
        vo.setAnimalBoardTitle(dto.getAnimalBoardTitle());
        vo.setAnimalBoardContent(dto.getAnimalBoardContent());
        AnimalCategory animalCategory = new AnimalCategory(); // board -> category로 animalCategoryCode 가져오기
        animalCategory.setAnimalCategoryCode(dto.getAnimalCategoryCode());
        vo.setAnimalCategory(animalCategory);
        AnimalBoard writtenBoard = animalBoardService.write(vo);
        log.info("vo : " + writtenBoard);

        // 글에 필요한 사진 넣기- 어떤 글의 사진? => animal_board_code 필요
        for(MultipartFile file : dto.getFiles()){
            log.info("file :" + file.getOriginalFilename());

            String fileName =  file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String saveName = uploadPath + File.separator + "animalBoard" + File.separator + uuid + "_" + fileName;
            Path savePath = Paths.get(saveName);
            file.transferTo(savePath);

            AnimalBoardImage image = new AnimalBoardImage();
            image.setAnimalBoardImage(saveName);
            image.setAnimalBoard(writtenBoard);
            animalBoardService.writeImages(image);

        }
        return writtenBoard !=null ? ResponseEntity.ok(writtenBoard) : ResponseEntity.badRequest().build();
    }
    // 자유게시판 글 전체 보기 - 추후에 무한스크롤 페이징 처리가 필요
    @GetMapping("/animal-board")
    public ResponseEntity<List<AnimalBoard>> viewAll(){
        List<AnimalBoard> list = animalBoardService.viewAll();

        return list!=null ? ResponseEntity.ok(list) : ResponseEntity.badRequest().build();
    }

    // 자유게시판 - 글 한개보기 = 게시판 상세보기
    @GetMapping("/animal-board/{animalBoardCode}")
    public ResponseEntity<AnimalBoard> viewDetail(@PathVariable(name = "animalBoardCode")int animalBoardCode){
        AnimalBoard board = animalBoardService.viewDetail(animalBoardCode);
        return board!=null ? ResponseEntity.ok(board) : ResponseEntity.notFound().build();
    }
    // 자유게시판 - 글 수정
    @PutMapping("/animal-board")
    public ResponseEntity<AnimalBoard> boardUpdate(AnimalBoardUpdateDTO dto) throws IOException {
        log.info("dto : " + dto);
        log.info("dto.code : " + dto.getAnimalBoardCode());

        // 글 수정을 위한 글 객체 생성
        AnimalBoard board = new AnimalBoard();
        board.setAnimalBoardCode(dto.getAnimalBoardCode());
        board.setAnimalBoardTitle(dto.getAnimalBoardTitle());
        board.setAnimalBoardContent(dto.getAnimalBoardContent());
        AnimalCategory animalCategory = new AnimalCategory();
        animalCategory.setAnimalCategoryCode(dto.getAnimalCategoryCode());
        board.setAnimalCategory(animalCategory);

        // 기존 데이터(사진) 가져오기 그리고 바로 삭제
        AnimalBoard writtenBoard= animalBoardService.viewDetail(dto.getAnimalBoardCode());
        if(writtenBoard.getImages()!=null){
            for(AnimalBoardImage image : writtenBoard.getImages()){

                for(MultipartFile file : dto.getFiles()){
                    if (file!=null && !image.equals(file)){
                        File filed = new File(String.valueOf(image));
                        filed.delete();
                        String fileName =  file.getOriginalFilename();
                        String uuid = UUID.randomUUID().toString();
                        String saveName = uploadPath + File.separator + "animalBoard" + File.separator + uuid + "_" + fileName;
                        Path savePath = Paths.get(saveName);
                        file.transferTo(savePath);

                        AnimalBoardImage imageResult = new AnimalBoardImage();
                        imageResult.setAnimalBoardImage(saveName);
                        imageResult.setAnimalBoard(writtenBoard);
                        animalBoardService.writeImages(imageResult);

//                        board.setImages(imageResult);
                    }
                }

            }


        }

       return null;

    }
    // 자유게시판 - 글 삭제
}
