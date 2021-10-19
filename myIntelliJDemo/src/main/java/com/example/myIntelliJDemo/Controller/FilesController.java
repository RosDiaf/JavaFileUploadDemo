package com.example.myIntelliJDemo.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.myIntelliJDemo.Message.ResponseMessage;
import com.example.myIntelliJDemo.Model.FileInfo;
import com.example.myIntelliJDemo.Service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
@CrossOrigin("http://localhost:8081")
public class FilesController {

    @Autowired
    FilesStorageService storageService;

    List<String> fileNameList = new ArrayList<>();
    List<String> UrlNameList = new ArrayList<>();

    @GetMapping("/form")
    public ModelAndView showUploadForm(Model model) {
        return new ModelAndView("upload");
    }

    /* Uploading by using the form */
    /* @ModelAttribute("fileInfo") FileInfo files */
    // @PostMapping("/uploading")
    // @RequestParam("name") MultipartFile[] name
    @RequestMapping(value = "/uploading", method=RequestMethod.POST)
    public ModelAndView uploadFilesByForm(@ModelAttribute("fileInfo") FileInfo files, BindingResult bindingResult, Model model) throws IOException {
        List<String> OriginalFileNames = new ArrayList<>();
        Arrays.asList(files).stream().forEach(file -> {
            file.getName().stream().forEach(name -> {
                System.out.println("Name: "+ name.getOriginalFilename());
                OriginalFileNames.add(name.getOriginalFilename());
            });
        });
        model.addAttribute("OriginalFileNames", OriginalFileNames);

        String message = "";
        try {
            List<String> fileNames = new ArrayList<>();

            Arrays.asList(files).stream().forEach(file -> {
                file.getName().stream().forEach(item -> {
                    storageService.save(item);
                    fileNames.add((item).getOriginalFilename());
                });
            });

            storageService.loadAll().map(path -> {
                System.out.println("PATH: " + path);
                String filename = path.getFileName().toString();
                String url = MvcUriComponentsBuilder
                        .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

                fileNameList.add(filename);
                UrlNameList.add(url);
                return true;
            });

            model.addAttribute("fileNameList", fileNameList);
            model.addAttribute("UrlNameList", UrlNameList);

//            model.addAttribute("fileInfo", fileInfo);
//
//            fileInfo.stream().forEach(item -> {
//                item.getName().stream().forEach(name -> {
//                    model.addAttribute("fileName", name.getOriginalFilename());
//                    try {
//                        model.addAttribute("inputStream", name.getInputStream());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });
//            });

            message = "Uploaded the files successfully: ";

        } catch (Exception e) {
            message = "Fail to upload files!";
        }

        model.addAttribute("message",
                message);

        return new ModelAndView("upload");
    }

    /* Uploading by using the POSTMAN */
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("files") MultipartFile[] files, Model model) {
        String message = "";
        try {
            List<String> fileNames = new ArrayList<>();

            Arrays.asList(files).stream().forEach(file -> {
                storageService.save(file);
                fileNames.add(file.getOriginalFilename());
            });

            message = "Uploaded the files successfully: " + fileNames;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Fail to upload files!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

//    @GetMapping("/files")
//    public ResponseEntity<List<FileInfo>> getListFiles() {
//        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
//            String filename = path.getFileName().toString();
//            String url = MvcUriComponentsBuilder
//                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
//
//            return new FileInfo(filename, url);
//        }).collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
//    }

//    @GetMapping("/files")
//    public ResponseEntity<List<FileInfo>> getListFiles() {
//        List<FileInfo> fileInfo = storageService.loadAll().map(path -> {
//            List<MultipartFile> filename = (List<MultipartFile>) path.getFileName();
//            String url = MvcUriComponentsBuilder
//                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
//
//            return new FileInfo(filename, url);
//        }).collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.OK).body(fileInfo);
//    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}