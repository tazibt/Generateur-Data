import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class GenerateUsers {

    static String[] firstNames = {
        "Alex", "Lucas", "Maxime", "Julien", "Evan", "Léo", "Nathan",
        "Thomas", "Arthur", "Enzo", "Hugo", "Noah", "Ethan", "Louis"
    };

    static String[] lastNames = {
        "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard",
        "Petit", "Durand", "Moreau", "Simon", "Laurent", "Lefevre"
    };

    static String[] cities = {
        "Paris", "Lyon", "Marseille", "Toulouse", "Nice",
        "Nantes", "Montpellier", "Strasbourg", "Bordeaux"
    };

    static String[] streets = {
        "Rue de la République", "Avenue de Paris", "Boulevard Victor Hugo",
        "Rue Nationale", "Rue Lafayette", "Rue du Général Leclerc"
    };

    static String[] genders = { "H", "F" };

    public static void main(String[] args) {

        try {
            Properties config = new Properties();
            config.load(new FileInputStream("config.properties"));

            int userCount = Integer.parseInt(config.getProperty("user.count"));
            boolean phoneEnabled = Boolean.parseBoolean(config.getProperty("phone.enabled"));
            boolean addressEnabled = Boolean.parseBoolean(config.getProperty("address.enabled"));
            boolean birthEnabled = Boolean.parseBoolean(config.getProperty("birthdate.enabled"));
            boolean genderEnabled = Boolean.parseBoolean(config.getProperty("gender.enabled"));
            boolean zipcodeEnabled = Boolean.parseBoolean(config.getProperty("zipcode.enabled"));
            boolean cityEnabled = Boolean.parseBoolean(config.getProperty("city.enabled"));
            boolean streetEnabled = Boolean.parseBoolean(config.getProperty("street.enabled"));
            boolean randomPassword = Boolean.parseBoolean(config.getProperty("password.random"));

            String phonePrefix = config.getProperty("phone.prefix");
            String emailDomain = config.getProperty("email.domain");

            int minYear = Integer.parseInt(config.getProperty("birthdate.minYear"));
            int maxYear = Integer.parseInt(config.getProperty("birthdate.maxYear"));

            Random rand = new Random();

            FileWriter csv = new FileWriter("users.csv");

            // Header dynamique
            csv.write("firstname,lastname,email,password");

            if (genderEnabled) csv.write(",gender");
            if (birthEnabled) csv.write(",birthdate");
            if (phoneEnabled) csv.write(",phone");
            if (streetEnabled) csv.write(",street");
            if (cityEnabled) csv.write(",city");
            if (zipcodeEnabled) csv.write(",zipcode");

            csv.write("\n");

            for (int i = 1; i <= userCount; i++) {

                String first = firstNames[rand.nextInt(firstNames.length)];
                String last = lastNames[rand.nextInt(lastNames.length)];

                String email = first.toLowerCase() + "." + last.toLowerCase() + i + "@" + emailDomain;

                String password = randomPassword
                        ? "Pass-" + UUID.randomUUID().toString().substring(0, 8)
                        : "Pass123";

                String gender = genders[rand.nextInt(genders.length)];

                int year = rand.nextInt(maxYear - minYear + 1) + minYear;
                int month = rand.nextInt(12) + 1;
                int day = rand.nextInt(28) + 1;
                String birthdate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);

                String phone = phonePrefix + String.format("%08d", i);

                String street = streets[rand.nextInt(streets.length)];
                String city = cities[rand.nextInt(cities.length)];
                String zipcode = String.format("%05d", rand.nextInt(99999));

                csv.write(first + "," + last + "," + email + "," + password);

                if (genderEnabled) csv.write("," + gender);
                if (birthEnabled) csv.write("," + birthdate);
                if (phoneEnabled) csv.write("," + phone);
                if (streetEnabled) csv.write("," + street);
                if (cityEnabled) csv.write("," + city);
                if (zipcodeEnabled) csv.write("," + zipcode);

                csv.write("\n");
            }

            csv.close();
            System.out.println("✔ users.csv généré avec toutes les données réalistes");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
