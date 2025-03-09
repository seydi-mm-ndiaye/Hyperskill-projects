package qrcodeapi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class Controller {

    private QRCodeWriter writer = new QRCodeWriter();


    @GetMapping("/health")
    public ResponseEntity<String> health() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/qrcode")
    public ResponseEntity<?> qrcode(@RequestParam("contents") @NonNull String contents, @RequestParam(value="size", defaultValue = "250") int size,
                                    @RequestParam(value = "type", defaultValue = "png") String type, @RequestParam(value = "correction", defaultValue = "L") String correction){

        if(contents.isBlank())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Contents cannot be null or blank\"}");
        else if((size < 150) || (size > 350))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Image size must be between 150 and 350 pixels\"}");
        else if (!List.of("L", "M", "Q", "H").contains(correction.toUpperCase()))
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Permitted error correction levels are L, M, Q, H\"}");
        else if (!List.of("png", "gif", "jpeg").contains(type)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Only png, jpeg and gif image types are supported\"}");
        }else{
            BufferedImage bufferedImage = generateImage(contents, size, correction);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("image/" + type))
                    .body(bufferedImage);
        }


    }

    public BufferedImage generateImage(String contents, int size, String correction) {

        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.valueOf(correction));

        try {
            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {

            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size, size);

            return image;
        }
    }
}