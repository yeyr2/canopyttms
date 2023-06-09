package com.ttms.controller;

import com.ttms.component.ResponseEntityComponent;
import com.ttms.dao.Dao;
import com.ttms.dao.StudioDao;
import com.ttms.pojo.Page;
import com.ttms.pojo.Response;
import com.ttms.pojo.Studio;
import com.ttms.pojo.TokenAndT;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/Studio")
public class StudioController {

    @Autowired
    private StudioDao sd;

    @RequestMapping(value = "/getStudio",method = RequestMethod.GET)
    public ResponseEntity<Response> getStudio(@RequestParam("token") String token,@RequestParam(name = "pageSize") Integer pageSize,@RequestParam("page") Integer page) throws Exception {
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Long totalPage = Dao.getNoMaxByPagesNum(pageSize, page,Studio.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }

        List<Studio> studios = sd.getStudio(pageSize,page,null,"");

        Response request = new Response();
        request.setValue(new Page<>(studios, totalPage, page, pageSize));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @RequestMapping(value = "/getStudioById",method = RequestMethod.GET)
    public ResponseEntity<Response> getStudioById(@RequestParam("token") String token,@RequestParam(name = "pageSize") Integer pageSize,@RequestParam("page") Integer page,@RequestParam("id") Long id) throws Exception {
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Long totalPage = Dao.getNoMaxByPagesNum(pageSize, page,Studio.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }

        List<Studio> studios = sd.getStudio(pageSize,page,id,"id");

        Response request = new Response();
        request.setValue(new Page<>(studios, totalPage, page, pageSize));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @RequestMapping(value = "/getStudioByName",method = RequestMethod.GET)
    public ResponseEntity<Response> getStudioByName(@RequestParam("token") String token,@RequestParam(name = "pageSize") Integer pageSize,@RequestParam("page") Integer page,@RequestParam("name") String name) throws Exception {
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Long totalPage = Dao.getNoMaxByPagesNum(pageSize, page,Studio.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }

        List<Studio> studios = sd.getStudio(pageSize,page,name,"name");

        Response request = new Response();
        request.setValue(new Page<>(studios, totalPage, page, pageSize));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    /**
     *
     * @param tokenAndStudio 传入name,StudioRows,StudioColumns,description
     */
    @RequestMapping(value = "/insertStudio",method = RequestMethod.POST)
    public ResponseEntity<Response> insertStudio(@RequestBody TokenAndT<Studio> tokenAndStudio) throws Exception {
        if (TokenUtils.verify(tokenAndStudio.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Studio studio = tokenAndStudio.getValue();
        if (studio.getId() != null || !studio.verify("insert")) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if (sd.verify(studio)) {
            return ResponseEntityComponent.Conflict("name");
        }
//        try{
            boolean status = sd.insertStudio(studio);
//        }catch (Exception e){
//            return ResponseEntityComponent.Create_Failed("studio");
//        }


        Response response =  new Response();
        response.setValue(studio.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/updateStudio",method = RequestMethod.POST)
    public ResponseEntity<Response> updateStudio(@RequestBody TokenAndT<Studio> tokenAndStudio) {
        if (TokenUtils.verify(tokenAndStudio.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Studio studio = tokenAndStudio.getValue();
        if (studio.getId() == null) {
            return ResponseEntityComponent.ID_ERR;
        }

        if (!studio.verify("update")) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if (studio.getName() != null && sd.verify(studio)) {
            return ResponseEntityComponent.Conflict("name");
        }

        boolean status = sd.updateStudio(studio);
        if (!status) {
            return ResponseEntityComponent.Update_Failed("studio");
        }

        Response response = new Response();
        response.setValue(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteStudio",method = RequestMethod.GET)
    public ResponseEntity<Response> deleteStudio(@RequestParam("token") String token,@RequestParam("id") Long id) {
        //TODO: 删除相关联的演出计划,删除座位
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        boolean status = sd.deleteStudio(id);
        if (!status) {
            return ResponseEntityComponent.Delete_Failed("studio");
        }

        Response response = new Response();
        response.setValue(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static Studio getStudio(HttpServletRequest request,String type) {
        String id = request.getParameter("id");
        Studio studio = new Studio();
        if (id == null && Objects.equals(type, "update")) {
            return null;
        }else{
            studio.setId(id == null ? null : Long.valueOf(id));
        }
        String name = request.getParameter("name");

        String studioRows = request.getParameter("StudioRows");
        Integer StudioRows = studioRows == null ? (type.equals("insert") ? 8 : null) : Integer.valueOf(studioRows);

        String studioColumns = request.getParameter("StudioColumns");
        Integer StudioColumns = studioColumns == null ? (type.equals("insert") ? 8 : null) : Integer.valueOf(studioColumns);

        String description = request.getParameter("description");

        return studio.getStudio(name,StudioRows,StudioColumns,description != null ? description : "");
    }
}
