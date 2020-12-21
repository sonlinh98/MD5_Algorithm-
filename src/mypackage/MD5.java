package mypackage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class MD5
{
	//Initialize MD Buffer
    private static final int   INIT_A     = 0x67452301;// 0x là tiền tố cho một số cho biết nó ở cơ số 16 (hex).
    private static final int   INIT_B     = 0xEFCDAB89;
    private static final int   INIT_C     = 0x98BADCFE;
    private static final int   INIT_D     = 0x10325476;
    
 // chỉ định số lượng dịch chuyển mỗi vòng
    private static final int[] SHIFT_AMTS = { 
		7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,
		5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,
		4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,
		6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21
    };
    
    
    
    private static final int[] TABLE_T    = new int[64];
    
    static
    {
    	// Sử dụng phần số nguyên nhị phân của các dãy số nguyên (Radian) làm hằng số:
        for (int i = 0; i < 64; i++)
            TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }
 
    public static byte[] computeMD5(byte[] message)
    {
    	// lấy độ dài của chuỗi(đơn vị byte)
        int messageLenBytes = message.length;
        
        // 8 - số byte dùng để lưu trữ độ dài của message
        // 6 - số bit dịch phải(2^6 = 64byte= 512bit=độ dài 1 block)
        int numBlocks = ((messageLenBytes + 8) >>> 6) + 1;// Tính để biết message chia được bao nhiêu block(64 byte=512bit)
        
        // Tính tổng độ dài của tổng số block(đơn vị byte). 
        int totalLen = numBlocks << 6;
        
        // Step 1. Append Padding Bits
        // Tính số byte cần thêm vào block cuối bị thiếu
        byte[] paddingBytes = new byte[totalLen - messageLenBytes];// khởi tạo tất cả bit của padding đều bằng bit 0
        
        // gán bit đầu tiên là bit 1
        paddingBytes[0] = (byte) 0x80;//10000000
        
        
        // Step 2. Append Length
        // Lưu trữ độ dài của message vào 64bit(= 8 byte) cuối của phần padding
        long messageLenBits = (long) messageLenBytes << 3;
        for (int i = 0; i < 8; i++)
        {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLenBits;
            messageLenBits >>>= 8;
        }
        
        // Step 3. Initialize MD Buffer
        int a = INIT_A;
        int b = INIT_B;
        int c = INIT_C;
        int d = INIT_D;
        
       // ngắt đoạn thành mười sáu từ 32 bit (4 byte) M [j], 0 ≤ j ≤ 15
        int[] M = new int[16];
        
        for (int i = 0; i < numBlocks; i++)
        {
            int index = i << 6;// i*64
            for (int j = 0; j < 64; j++, index++) // duyệt hết 1 block
                M[j >>> 2] = ((int) ((index < messageLenBytes) ? message[index]
                        : paddingBytes[index - messageLenBytes]) << 24)
                        | (M[j >>> 2] >>> 8);
            
            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;

            for (int j = 0; j < 64; j++)
            {
            	//Step 4. Process Message in 16-Word Blocks
                int div16 = j / 16;
                int f = 0;
                int g = j;
                switch (div16)
                {
                    case 0:
                        f = (b & c) | (~b & d);
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);
                        g = (g * 5 + 1) % 16;
                        break;
                    case 2:
                        f = b ^ c ^ d;
                        g = (g * 3 + 5) % 16;
                        break;
                    case 3:
                        f = c ^ (b | ~d);
                        g = (g * 7) % 16;
                        break;
                }
                int temp = b + Integer.rotateLeft(a + f + M[g] + TABLE_T[j], SHIFT_AMTS[j]);
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }
        
        byte[] md5 = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++)
        {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++)
            {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md5;
    }
 
    public static String toHexString(byte[] b)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
        {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }
 
    public static void main(String[] args)
    {
    	
    	System.err.println("------------Ham md5 cua toi-----------");
    	String s = "SanfoundrySanfoundrySanfoundrySanfoundrySanfoundrySanfoundry";
    	System.out.println(toHexString(computeMD5(s.getBytes())) + " <== \"" + s + "\"");
        
        
        
        
        
        // Sử dụng hàm được xây dựng sẵn trong java
        System.err.println("------------Su dung ham duoc xay dung trong java-----------");
        String password = "SanfoundrySanfoundrySanfoundrySanfoundrySanfoundrySanfoundry";
        
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        String md5 = DatatypeConverter.printHexBinary(digest);
 
        // print MD5 Message Digest
        System.out.println(md5+ " <== \"" + password + "\"");
        return;
    }
}