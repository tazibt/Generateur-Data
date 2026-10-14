import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class GenerateUsers {

    static String[] maleNames = {
        "Alex", "Lucas", "Maxime", "Julien", "Evan", "Léo", "Nathan",
        "Thomas", "Arthur", "Enzo", "Hugo", "Noah", "Ethan", "Louis"
    };

    static String[] femaleNames = {
        "Emma", "Jade", "Louise", "Alice", "Chloé", "Léa", "Manon",
        "Inès", "Camille", "Sarah", "Julie", "Clara", "Zoé", "Anna"
    };

    static String[] lastNames = {
        "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard",
        "Petit", "Durand", "Moreau", "Simon", "Laurent", "Lefevre"
    };

    static String[][] cities = {
        {"Paris", "750"}, {"Lyon", "690"}, {"Marseille", "130"}, {"Toulouse", "310"},
        {"Nice", "060"}, {"Nantes", "440"}, {"Montpellier", "340"},
        {"Strasbourg", "670"}, {"Bordeaux", "330"}
    };

    static String[] streets = {
        "Rue de la République", "Avenue de Paris", "Boulevard Victor Hugo",
        "Rue Nationale", "Rue Lafayette", "Rue du Général Leclerc"
    };

    public static void main(String[] args) throws IOException {

        String configFile = args.length > 0 ? args[0] : "config.properties";
        Properties config = new Properties();
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            config.load(in);
        }

        int userCount = Integer.parseInt(config.getProperty("user.count", "100"));
        Path output = Path.of(config.getProperty("output.file", "users.csv"));

        boolean phoneEnabled = Boolean.parseBoolean(config.getProperty("phone.enabled", "true"));
        boolean addressEnabled = Boolean.parseBoolean(config.getProperty("address.enabled", "true"));
        boolean birthEnabled = Boolean.parseBoolean(config.getProperty("birthdate.enabled", "true"));
        boolean genderEnabled = Boolean.parseBoolean(config.getProperty("gender.enabled", "true"));
        boolean randomPassword = Boolean.parseBoolean(config.getProperty("password.random", "true"));

        String phonePrefix = config.getProperty("phone.prefix", "063998");
        String emailDomain = config.getProperty("email.domain", "example.com");

        int minYear = Integer.parseInt(config.getProperty("birthdate.minYear", "1970"));
        int maxYear = Integer.parseInt(config.getProperty("birthdate.maxYear", "2005"));

        // même seed = même fichier, pratique pour rejouer un test
        String seed = config.getProperty("random.seed", "").trim();
        Random rand = seed.isEmpty() ? new Random() : new Random(Long.parseLong(seed));

        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }

        try (Writer csv = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {

            // Header dynamique
            csv.write("firstname,lastname,email,password");
            if (genderEnabled) csv.write(",gender");
            if (birthEnabled) csv.write(",birthdate");
            if (phoneEnabled) csv.write(",phone");
            if (addressEnabled) csv.write(",street,city,zipcode");
            csv.write("\n");

            for (int i = 1; i <= userCount; i++) {

                boolean female = rand.nextBoolean();
                String gender = female ? "F" : "H";
                String[] names = female ? femaleNames : maleNames;
                String first = names[rand.nextInt(names.length)];
                String last = lastNames[rand.nextInt(lastNames.length)];

                String email = normalize(first) + "." + normalize(last) + i + "@" + emailDomain;

                String password = randomPassword
                        ? "Pass-" + new UUID(rand.nextLong(), rand.nextLong()).toString().substring(0, 8)
                        : "Pass123";

                csv.write(first + "," + last + "," + email + "," + password);

                if (genderEnabled) {
                    csv.write("," + gender);
                }
                if (birthEnabled) {
                    int year = rand.nextInt(maxYear - minYear + 1) + minYear;
                    int month = rand.nextInt(12) + 1;
                    int day = rand.nextInt(28) + 1;
                    csv.write("," + year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));
                }
                if (phoneEnabled) {
                    csv.write("," + phonePrefix + String.format("%04d", i % 10000));
                }
                if (addressEnabled) {
                    String[] city = cities[rand.nextInt(cities.length)];
                    String street = (rand.nextInt(150) + 1) + " " + streets[rand.nextInt(streets.length)];
                    String zipcode = city[1] + String.format("%02d", rand.nextInt(20) + 1);
                    csv.write("," + street + "," + city[0] + "," + zipcode);
                }

                csv.write("\n");
            }
        }

        System.out.println(userCount + " utilisateurs générés dans " + output);
    }

    // enlève les accents pour les emails
    static String normalize(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }
}
