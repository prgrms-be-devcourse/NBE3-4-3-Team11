package com.pofo.backend.domain.project.service;

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class FileService(
    @Value("\${file.upload-dir}") private val uploadDir: String // 파일 저장할 기본 경로
){

    // 썸네일 업로드 메서드
    fun uploadThumbnail(file: MultipartFile?): String {

        require(!(file == null || file.isEmpty())) {"파일이 비어 있습니다."}

        return try {
            // 1. 업로드 디렉토리 생성 (없다면 생성)
            val directory = File(uploadDir)
            if (!directory.exists()) {
                directory.mkdirs(); //디렉토리 자동 생성
            }

            // 2. 파일 저장 이름 설정 (UUID 활용)
            val fileName = "${UUID.randomUUID()}_${file.getOriginalFilename()}"
            val filePath: Path = Paths.get(uploadDir, fileName)

            // 3. 파일 저장
            file.transferTo(filePath.toFile())

            // 4. 저장된 파일 경로 반환
            "http://localhost:8080/uploads/${fileName}"
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("파일 업로드 중 오류가 발생했습니다.", e)
        }
    }

    // 썸네일 삭제 메서드
    fun deleteFile(filePath: String?) {

        if(filePath.isNullOrBlank()) return

        //저장된 파일의 물리적 경로를 가져와 삭제
        val fileName = filePath.replace("http://localhost:8080/uploads/", "");
        val file = File("$uploadDir/$fileName")

        if (file.exists() && !file.delete()) {
            throw RuntimeException("파일 삭제 실패: $filePath")
        }
    }
}
