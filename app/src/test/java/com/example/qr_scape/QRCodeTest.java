package com.example.qr_scape;



import static com.example.qr_scape.QRCode.calculateScore;

import com.google.common.hash.Hashing;

//import org.junit.*;
//import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;

public class QRCodeTest {


    @Test
    public void getQRHashTest(){
        QRCode qrCode = new QRCode("ThisIsTheQRText", "TyGreve", 142.23463, -133.123234, "Photo");
        final String QRTextHash = Hashing.sha256()
                .hashString("ThisIsTheQRText", StandardCharsets.UTF_8)
                .toString();
        assertEquals(QRTextHash,qrCode.getQRHash());
    }

    @Test
    public void setQRHashTest(){
        QRCode qrCode = new QRCode("ThisIsTheQRText", "TyGreve", 142.23463, -133.123234, "Photo");
        final String QRTextHash = Hashing.sha256()
                .hashString("ThisIsTheQRText", StandardCharsets.UTF_8)
                .toString();
        qrCode.setQRHash(QRTextHash);
        assertEquals(QRTextHash,qrCode.getQRHash());
    }

    @Test
    public void getQRHashSaltedTest(){
        QRCode qrCode = new QRCode("ThisIsTheQRText", "TyGreve", 142.23463, -133.123234, "Photo");
        final String QRTextHashSalted = Hashing.sha256()
                .hashString("ThisIsTheQRTextTyGreve", StandardCharsets.UTF_8)
                .toString();
        assertEquals(QRTextHashSalted,qrCode.getQRHashSalted());
    }

    @Test
    public void setQRHashSaltedTest(){
        QRCode qrCode = new QRCode("ThisIsTheQRText", "TyGreve", 142.23463, -133.123234, "Photo");
        final String QRTextHashSalted = Hashing.sha256()
                .hashString("ThisIsTheQRTextTyGreve", StandardCharsets.UTF_8)
                .toString();
        qrCode.setQRHashSalted(QRTextHashSalted);
        assertEquals(QRTextHashSalted,qrCode.getQRHashSalted());
    }

    @Test
    public void getScoreTest() {
        String givenHash = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        int expectedScore = 111;
        assertEquals(expectedScore, calculateScore(givenHash));
    }

}
