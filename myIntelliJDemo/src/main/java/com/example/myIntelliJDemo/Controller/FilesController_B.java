package com.example.myIntelliJDemo.Controller;

import com.example.myIntelliJDemo.Model.FileResponse;
import com.example.myIntelliJDemo.Service.FileStorageService_B;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FilesController_B {
    private FileStorageService_B storageService;

    public FilesController_B(FileStorageService_B storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listAllFiles(Model model) {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/uploads/")
                                .path(path.getFileName().toString())
                                .toUriString())
                .collect(Collectors.toList()));

        return "listFiles";
    }

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload-file")
    @ResponseBody
    public RedirectView uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        uploadFileHelper(file);
        return new RedirectView("/");
    }

    @PostMapping("/upload-multiple-files")
    @ResponseBody
    public RedirectView uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        Arrays.stream(files)
                .map(file -> uploadFileHelper(file))
                .collect(Collectors.toList());
        return new RedirectView("/");
    }

    @GetMapping("/delete-file/{id}")
    public RedirectView deleteFile(@PathVariable("id") String id) {
        System.out.println("Delete: " + id);
        return new RedirectView("/");
    }

    @GetMapping("/delete-all")
    public RedirectView deleteAllFile() {
        storageService.deleteAll();
        return new RedirectView("/");
    }

    public FileResponse uploadFileHelper(MultipartFile file) {
        String name = storageService.store(file);

        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(name)
                .toUriString();

        return new FileResponse(name, uri, file.getContentType(), file.getSize());
    }
}
