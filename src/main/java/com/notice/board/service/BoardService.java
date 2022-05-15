package com.notice.board.service;

import com.notice.board.entity.Store;
import com.notice.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service     //spring bean 사용하여 따로 설정했으므로 삭제해야함
public class BoardService {

    @Autowired
    private BoardRepository repository;

    public BoardService(BoardRepository repository){
        this.repository = repository;
    }

    public void storewrite(Store store, MultipartFile file) throws IOException {

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";        // 파일 경로 저장

        UUID uuid = UUID.randomUUID();      // 식별자 , 파일 이름을 랜덤으로 저장하기 위해

        String filename = uuid + "_" + file.getOriginalFilename();      // 파일 이름 변수에 랜덤이름 저장 => uuid(랜덤 이름)_업로드한파일명

        File saveFile = new File(projectPath,filename);   // projectpath 경로에 name이름으로 저장할것

        file.transferTo(saveFile);

        store.setStorefilename(filename);                   // 파일 이름  db 저장
        store.setStorefilepath("/files/"+filename);         // 파일 경로 db 저장

        repository.save(store);
    }

    public List<Store> storelist(){
        return repository.findAll();
    }

    public Store storedetail(int id){
        return repository.findById(id).get();
    }

    public void storedelete(int id){

        repository.deleteById(id);
    }
}
