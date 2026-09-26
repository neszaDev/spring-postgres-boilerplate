import java.nio.file.*;
import java.io.*;

public class RemoveBom {
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println( Usage: RemoveBom file1 ...);
      System.exit(1);
    }
    for (String p : args) {
      Path path = Paths.get(p);
      byte[] b = Files.readAllBytes(path);
      if (b.length >= 3 && (b[0]==(byte)0xEF && b[1]==(byte)0xBB && b[2]==(byte)0xBF)) {
        byte[] nb = new byte[b.length-3];
        System.arraycopy(b, 3, nb, 0, nb.length);
        Files.write(path, nb);
        System.out.println(Fixed  + p);
      } else {
        System.out.println( No BOM:  + p);
      }
    }
  }
}
