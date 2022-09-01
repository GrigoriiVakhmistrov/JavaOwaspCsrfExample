import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GenerateToken {
    public static void main(String[] args) {
        //var now = OffsetDateTime.now(ZoneOffset.UTC);
        var now = OffsetDateTime.parse("2022-08-28 23:15:52.136045",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone(ZoneOffset.UTC));
        var random = new Random();
        random.setSeed(now.toEpochSecond());

        //2022-08-29 00:19:00.864463
        System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
        System.out.println(now.toEpochSecond());
        System.out.println(random.nextLong());
    }
}
