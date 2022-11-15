package com.tms.training.controller;

import com.tms.training.annotation.LoginRequired;
import com.tms.training.dao.LoginTicketMapper;
import com.tms.training.entity.User;
import com.tms.training.service.UserService;
import com.tms.training.util.CommnityUtil;
import com.tms.training.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile multipartFile, Model model){
        if (multipartFile==null){
            model.addAttribute("error","请上传你想要的图片");
            return "/site/setting";
        }
        String filename = multipartFile.getOriginalFilename();
        String substring = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(substring)){
            model.addAttribute("error","文件格式错误！");
            return "/site/setting";
        }
        filename = CommnityUtil.generateUUID() + substring;
        File file = new File(uploadPath + "/" + filename);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            logger.error("上传头像失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/homepage";
    }
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        fileName = uploadPath + "/" + fileName;
        String substring = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+substring);
        try (
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(fileName);
        ){
            byte[] bytes = new byte[1024];
            int b = 0;
            while ((b=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败！"+e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public String updatePassword(String password,String newPassword,String newPassword2,Model model,@CookieValue("ticket") String ticket){
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user, password, newPassword,newPassword2);
        if (map.isEmpty()){
            loginTicketMapper.updateStatus(ticket, 1);
            return "redirect:/logout";
        }else {
            model.addAttribute("password",password);
            model.addAttribute("newPassword",newPassword);
            model.addAttribute("newPasswordMsg2","两次密码不相同");
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }

}
