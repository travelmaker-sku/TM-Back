package com.notice.board.controller;


import com.notice.board.entity.Total;
import com.notice.board.service.TotalBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/total")
public class TotalController {

    TotalBoardService service;

    @Autowired
    public TotalController(TotalBoardService service) {         // controller와 service 연결하는 느낌
        this.service = service;
    }


    @GetMapping("/new")
    public String create() {

        return "/total/Totalcreate";
    }

    // URL 이 변경되지 않은 상태에서 실행
    @PostMapping("/new")           // 게시글 작성
    public String createData(Total total, MultipartFile file) throws IOException {

        service.totalwrite(total, file);
        return "redirect:/ ";    // 제일 첫 페이지로 돌아감
    }

    @GetMapping("/findall")                // 게시글 전체 출력
    public String findAll(Model model, @PageableDefault(page = 0, size = 3, sort = "totalid", direction = Sort.Direction.DESC)
            Pageable pageable, Total total) {


        Page<Total> list = service.totallist(pageable);

        int nowpage = list.getPageable().getPageNumber() + 1;    // 게시물 아래의 페이지 번호 구현을 위한 설정 - 현재 페이지
        int startpage = Math.max(nowpage - 4, 1);                 // 현재 페이지 기준으로 앞쪽으로 4개의 페이지 출력
        // 만약 현재 페이지가 1이면 -3이 출력되므로 최소 1로 설정
        int endpage = Math.min(nowpage + 5, list.getTotalPages()); // 마지막 페이지까지 출력

        model.addAttribute("list", list);
        model.addAttribute("nowpage", nowpage);      // html에서 출력시키기 위해 model을 사용하여  값 전송
        model.addAttribute("startpage", startpage);
        model.addAttribute("endpage", endpage);

        return "total/Totalfindall";
    }

    @GetMapping("/findall/store")                // 게시글 맛집 전체 출력
    public String findStoreAll(Model model, @PageableDefault(page = 0, size = 3, sort = "totalid", direction = Sort.Direction.DESC)
            Pageable pageable, Total total) {


        String subject = "맛집";


        Page<Total> list = service.selectAllSQL(subject, pageable);

        int nowpage = list.getPageable().getPageNumber() + 1;    // 게시물 아래의 페이지 번호 구현을 위한 설정 - 현재 페이지
        int startpage = Math.max(nowpage - 4, 1);                 // 현재 페이지 기준으로 앞쪽으로 4개의 페이지 출력
        // 만약 현재 페이지가 1이면 -3이 출력되므로 최소 1로 설정
        int endpage = Math.min(nowpage + 5, list.getTotalPages()); // 마지막 페이지까지 출력

        model.addAttribute("list", list);
        model.addAttribute("nowpage", nowpage);      // html에서 출력시키기 위해 model을 사용하여  값 전송
        model.addAttribute("startpage", startpage);
        model.addAttribute("endpage", endpage);

        return "total/Totalstorefindall";
    }

    @GetMapping("/findall/lodging")                // 게시글 숙소 전체 출력
    public String findLogingAll(Model model, @PageableDefault(page = 0, size = 3, sort = "totalid", direction = Sort.Direction.DESC)
            Pageable pageable, Total total) {


        String subject = "숙소";


        Page<Total> list = service.selectAllSQL(subject, pageable);

        int nowpage = list.getPageable().getPageNumber() + 1;    // 게시물 아래의 페이지 번호 구현을 위한 설정 - 현재 페이지
        int startpage = Math.max(nowpage - 4, 1);                 // 현재 페이지 기준으로 앞쪽으로 4개의 페이지 출력
        // 만약 현재 페이지가 1이면 -3이 출력되므로 최소 1로 설정
        int endpage = Math.min(nowpage + 5, list.getTotalPages()); // 마지막 페이지까지 출력

        model.addAttribute("list", list);
        model.addAttribute("nowpage", nowpage);      // html에서 출력시키기 위해 model을 사용하여  값 전송
        model.addAttribute("startpage", startpage);
        model.addAttribute("endpage", endpage);

        return "total/Totallodgingfindall";
    }

    @GetMapping("/findall/place")                // 게시글 명소 전체 출력
    public String findPlaceAll(Model model, @PageableDefault(page = 0, size = 3, sort = "totalid", direction = Sort.Direction.DESC)
            Pageable pageable, Total total) {


        String subject = "명소";


        Page<Total> list = service.selectAllSQL(subject, pageable);

        int nowpage = list.getPageable().getPageNumber() + 1;    // 게시물 아래의 페이지 번호 구현을 위한 설정 - 현재 페이지
        int startpage = Math.max(nowpage - 4, 1);                 // 현재 페이지 기준으로 앞쪽으로 4개의 페이지 출력
        // 만약 현재 페이지가 1이면 -3이 출력되므로 최소 1로 설정
        int endpage = Math.min(nowpage + 5, list.getTotalPages()); // 마지막 페이지까지 출력

        model.addAttribute("list", list);
        model.addAttribute("nowpage", nowpage);      // html에서 출력시키기 위해 model을 사용하여  값 전송
        model.addAttribute("startpage", startpage);
        model.addAttribute("endpage", endpage);

        return "total/Totalplacefindall";
    }


    @GetMapping("/finddetail/{id}")         // 게시물 클릭시 세부 페이지 이동
    public String findTotaldetail(Model model, @PathVariable(value = "id") int id, Total total) {
        Total view = service.totaldetail(id);
        int result = view.getTotalviewcount();
        view.setTotalviewcount(result + 1);

        model.addAttribute("data", service.totaldetail(id));

        return "total/Totalfinddetail";
    }

 /*   @PostMapping("/finddetail/{id}")           // 좋와요 버튼 클릭시
    public String like(Total total,@PathVariable(value = "id") int id) {
        Total page = service.totaldetail(id);
            System.out.println("dd");
        System.out.println(page.getTotalbookmark());
        if(page.getTotalbookmark() == null) {
            service.like(total.getTotalbookmark(),id);
        //    page.setTotalbookmark(total.getTotalbookmark());
            System.out.println("좋아요 추가");
        }else{
            page.setTotalbookmark(null);
            System.out.println("좋아요 삭제");
        }
        return "redirect:/finddetail";    // 제일 첫 페이지로 돌아감
    }   */

/*    @PostMapping("/finddetail/{id}")           // 좋와요 버튼 클릭시 (update 쿼리문 사용)
    public String like(Total total,@PathVariable(value = "id") int id) {
        Total page = service.totaldetail(id);
        System.out.println("dd");
        System.out.println(page.getTotalbookmark());
        if(page.getTotalbookmark() == null) {
            service.like(total.getTotalbookmark(),id);
            //    page.setTotalbookmark(total.getTotalbookmark());
            System.out.println("좋아요 추가");
        }else{
            page.setTotalbookmark(null);
            System.out.println("좋아요 삭제");
        }
        return "redirect:/finddetail";    // 제일 첫 페이지로 돌아감
    }
  */



//    @GetMapping("/finddetail/{id}")         // 게시물 클릭시 세부 페이지 이동
//    public void viewcount(@PathVariable("id")int id, Model model, Store store){
//        Store view = service.storedetail(id);
//        int result = store.getStoreviewcount();
//        view.setStoreviewcount(result);
//        System.out.println(result);
//    }

//    @GetMapping("/finddetail/{id}")     // 조회수 증가
//    public void read(@PathVariable Long id, Model model) {
//        Store dto = service.storeview(id);
//        service.updateView(id); // views ++
//        model.addAttribute("posts", dto);
//    }


    @GetMapping("/delete")         // 게시물 삭제
    public String Totaldelete(int id) {
        service.totaldelete(id);
        return "redirect:/total/findall";
    }

    @GetMapping("/modify/{id}")            // 게시물 수정을 위한 수정 form
    public String Totalmodify(@PathVariable("id") int id, Model model) {
        model.addAttribute("data", service.totaleditdetail(id));
        return "total/Totalmodify";
    }


    @PostMapping("/update/{id}")        // 게시물 수정
    public String Storeupdate(@PathVariable("id") int id, Total total) {
        Total totaltemp = service.totaleditdetail(id);

        totaltemp.setTotalname(total.getTotalname());       // 기존의 내용중 이름을 새로운 값으로 덮어씌움
        totaltemp.setTotallocation(total.getTotallocation());
        totaltemp.setTotalmenu(total.getTotalmenu());
        totaltemp.setTotalprice(total.getTotalprice());
        totaltemp.setTotalscore(total.getTotalscore());
        totaltemp.setTotaltag(total.getTotaltag());
        totaltemp.setTotalsubject(total.getTotalsubject());

        service.totaleditwrite(totaltemp);
        return "redirect:/total/findall";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword") String keyword, Model model) {             // 객체로 받을때는 @ModelAttribute 사용

        List<Total> searchtagList = service.tagsearch(keyword);

        model.addAttribute("searchList", searchtagList);
        return "total/Totalsearchfindall";

        // findBy[ ]를 통한 검색기능
        /*
        List<Total> searchlocationList = service.locationsearch(search.getLocationkey());
        List<Total> searchsubjectList = service.subjectsearch(search.getSubjectkey(),search.getLocationkey());
        List<Total> searchtagList = service.tagsearch(search.getTagkey(),search.getLocationkey(),search.getSubjectkey());

        if(search.getLocationkey() != null) {
            model.addAttribute("searchList", searchlocationList);
        }
        if(search.getSubjectkey() != null) {
            model.addAttribute("searchList", searchsubjectList);
        }
        if(search.getTagkey() != null){
            model.addAttribute("searchList",searchtagList);
            }
        return "total/Totalsearchfindall";
        }

         */
    }
}
