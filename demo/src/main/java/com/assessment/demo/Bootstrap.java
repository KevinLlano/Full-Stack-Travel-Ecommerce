package com.assessment.demo;

import com.assessment.demo.dao.*;
import com.assessment.demo.entities.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class Bootstrap implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final DivisionRepository divisionRepository;
    private final CountryRepository countryRepository;
    private final VacationRepository vacationRepository;
    private final ExcursionRepository excursionRepository;

    public Bootstrap(CustomerRepository customerRepository, DivisionRepository divisionRepository,
                    CountryRepository countryRepository, VacationRepository vacationRepository,
                    ExcursionRepository excursionRepository) {
        this.customerRepository = customerRepository;
        this.divisionRepository = divisionRepository;
        this.countryRepository = countryRepository;
        this.vacationRepository = vacationRepository;
        this.excursionRepository = excursionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Bootstrap running...");
        System.out.println("Countries count: " + countryRepository.count());
        System.out.println("Divisions count: " + divisionRepository.count());
        System.out.println("Customers count: " + customerRepository.count());
        System.out.println("Vacations count: " + vacationRepository.count());
        System.out.println("Excursions count: " + excursionRepository.count());

        // Check if vacation data needs to be populated
        if (vacationRepository.count() == 0) {
            System.out.println("No vacation data found, populating with sample vacations and excursions...");
            populateVacationData();
        }

        // Only populate basic data if the database is completely empty
        if (countryRepository.count() == 0 && divisionRepository.count() == 0 && customerRepository.count() == 0) {
            System.out.println("Database appears empty, populating with sample data...");
            populateDatabase();
        } else {
            System.out.println("Database already contains basic data");
        }
    }

    private void populateVacationData() {
        // Create vacation packages with proper image URLs
        Vacation beachVacation = new Vacation();
        beachVacation.setVacation_title("Beach Paradise");
        beachVacation.setDescription("Tropical beach vacation with white sand beaches");
        beachVacation.setTravel_price(new BigDecimal("1500.00"));
        beachVacation.setImage_URL("https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=500&h=300&fit=crop");

        Vacation mountainVacation = new Vacation();
        mountainVacation.setVacation_title("Mountain Adventure");
        mountainVacation.setDescription("Exciting mountain hiking and outdoor activities");
        mountainVacation.setTravel_price(new BigDecimal("1200.00"));
        mountainVacation.setImage_URL("https://images.unsplash.com/photo-1464822759844-d150997c0904?w=500&h=300&fit=crop");

        Vacation cityVacation = new Vacation();
        cityVacation.setVacation_title("City Explorer");
        cityVacation.setDescription("Urban adventure with museums and cultural sites");
        cityVacation.setTravel_price(new BigDecimal("800.00"));
        cityVacation.setImage_URL("https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=500&h=300&fit=crop");

        Vacation safariVacation = new Vacation();
        safariVacation.setVacation_title("African Safari");
        safariVacation.setDescription("Wildlife safari adventure in Africa");
        safariVacation.setTravel_price(new BigDecimal("2500.00"));
        safariVacation.setImage_URL("https://images.unsplash.com/photo-1516426122078-c23e76319801?w=500&h=300&fit=crop");

        Vacation cruiseVacation = new Vacation();
        cruiseVacation.setVacation_title("Caribbean Cruise");
        cruiseVacation.setDescription("Luxury cruise through the Caribbean islands");
        cruiseVacation.setTravel_price(new BigDecimal("1800.00"));
        cruiseVacation.setImage_URL("https://images.unsplash.com/photo-1570472354071-d3015f50b4c4?w=500&h=300&fit=crop");

        // Save vacations
        List<Vacation> vacations = vacationRepository.saveAll(Arrays.asList(
                beachVacation, mountainVacation, cityVacation, safariVacation, cruiseVacation));

        // Create excursions for each vacation
        createExcursionsForVacation(vacations.get(0), "Beach Paradise"); // Beach
        createExcursionsForVacation(vacations.get(1), "Mountain Adventure"); // Mountain
        createExcursionsForVacation(vacations.get(2), "City Explorer"); // City
        createExcursionsForVacation(vacations.get(3), "African Safari"); // Safari
        createExcursionsForVacation(vacations.get(4), "Caribbean Cruise"); // Cruise

        System.out.println("Vacation and excursion data populated successfully!");
    }

    private void createExcursionsForVacation(Vacation vacation, String theme) {
        switch (theme) {
            case "Beach Paradise":
                createExcursion("Snorkeling Tour", new BigDecimal("75.00"),
                        "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=250&fit=crop", vacation);
                createExcursion("Sunset Cruise", new BigDecimal("120.00"),
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=250&fit=crop", vacation);
                createExcursion("Dolphin Watching", new BigDecimal("95.00"),
                        "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=250&fit=crop", vacation);
                break;
            case "Mountain Adventure":
                createExcursion("Rock Climbing", new BigDecimal("90.00"),
                        "https://images.unsplash.com/photo-1464822759844-d150997c0904?w=400&h=250&fit=crop", vacation);
                createExcursion("Hiking Trail", new BigDecimal("65.00"),
                        "https://images.unsplash.com/photo-1551632811-561732d1e306?w=400&h=250&fit=crop", vacation);
                createExcursion("Zip Line Adventure", new BigDecimal("110.00"),
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=250&fit=crop", vacation);
                break;
            case "City Explorer":
                createExcursion("City Walking Tour", new BigDecimal("45.00"),
                        "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=400&h=250&fit=crop", vacation);
                createExcursion("Museum Pass", new BigDecimal("35.00"),
                        "https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=400&h=250&fit=crop", vacation);
                createExcursion("Food Tour", new BigDecimal("85.00"),
                        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&h=250&fit=crop", vacation);
                break;
            case "African Safari":
                createExcursion("Big Five Safari", new BigDecimal("180.00"),
                        "https://images.unsplash.com/photo-1516426122078-c23e76319801?w=400&h=250&fit=crop", vacation);
                createExcursion("Hot Air Balloon", new BigDecimal("250.00"),
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=250&fit=crop", vacation);
                createExcursion("Cultural Village Visit", new BigDecimal("120.00"),
                        "https://images.unsplash.com/photo-1516426122078-c23e76319801?w=400&h=250&fit=crop", vacation);
                break;
            case "Caribbean Cruise":
                createExcursion("Scuba Diving", new BigDecimal("130.00"),
                        "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=250&fit=crop", vacation);
                createExcursion("Island Hopping", new BigDecimal("160.00"),
                        "https://images.unsplash.com/photo-1570472354071-d3015f50b4c4?w=400&h=250&fit=crop", vacation);
                createExcursion("Beach Volleyball", new BigDecimal("40.00"),
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=250&fit=crop", vacation);
                break;
        }
    }

    private void createExcursion(String title, BigDecimal price, String imageUrl, Vacation vacation) {
        Excursion excursion = new Excursion();
        excursion.setExcursion_title(title);
        excursion.setExcursion_price(price);
        excursion.setImage_URL(imageUrl);
        excursion.setVacation(vacation);
        excursionRepository.save(excursion);
    }

    private void populateDatabase() {
        // Create and save countries
        Country us = new Country();
        us.setId(1L);
        us.setCountry_name("United States");
        us.setCreate_date(new Date());
        us.setLast_update(new Date());

        Country uk = new Country();
        uk.setId(2L);
        uk.setCountry_name("United Kingdom");
        uk.setCreate_date(new Date());
        uk.setLast_update(new Date());

        Country canada = new Country();
        canada.setId(3L);
        canada.setCountry_name("Canada");
        canada.setCreate_date(new Date());
        canada.setLast_update(new Date());

        countryRepository.saveAll(Arrays.asList(us, uk, canada));

        // Create US divisions
        Division alabama = new Division(1L, "Alabama");
        alabama.setCountry_id(1L);
        alabama.setCountry(us);

        Division alaska = new Division(2L, "Alaska");
        alaska.setCountry_id(1L);
        alaska.setCountry(us);

        Division arizona = new Division(3L, "Arizona");
        arizona.setCountry_id(1L);
        arizona.setCountry(us);

        Division california = new Division(4L, "California");
        california.setCountry_id(1L);
        california.setCountry(us);

        // Create UK divisions
        Division england = new Division(101L, "England");
        england.setCountry_id(2L);
        england.setCountry(uk);

        Division scotland = new Division(102L, "Scotland");
        scotland.setCountry_id(2L);
        scotland.setCountry(uk);

        Division wales = new Division(103L, "Wales");
        wales.setCountry_id(2L);
        wales.setCountry(uk);

        // Create Canada divisions
        Division alberta = new Division(38L, "Alberta");
        alberta.setCountry_id(3L);
        alberta.setCountry(canada);

        Division britishColumbia = new Division(39L, "British Columbia");
        britishColumbia.setCountry_id(3L);
        britishColumbia.setCountry(canada);

        Division manitoba = new Division(40L, "Manitoba");
        manitoba.setCountry_id(3L);
        manitoba.setCountry(canada);

        Division ontario = new Division(46L, "Ontario");
        ontario.setCountry_id(3L);
        ontario.setCountry(canada);

        // Save all divisions
        List<Division> divisions = Arrays.asList(
                alabama, alaska, arizona, california,
                england, scotland, wales,
                alberta, britishColumbia, manitoba, ontario
        );
        divisionRepository.saveAll(divisions);

        // Add sample customers
        Division tonyDiv = divisionRepository.findById(4L).orElse(null); // California
        Division peterDiv = divisionRepository.findById(38L).orElse(null); // Alberta
        Division sherlockDiv = divisionRepository.findById(101L).orElse(null); // England
        Division frasierDiv = divisionRepository.findById(46L).orElse(null); // Ontario
        Division poirotDiv = divisionRepository.findById(101L).orElse(null); // England

        Customer customerOne = new Customer(1L, "John", "Doe", "123 Main St", "12345", "(123)456-7890", null, null, tonyDiv);
        Customer tony = new Customer(2L, "Tony", "Stark", "10880 Malibu Point", "90265", "(123)456-7890", null, null, tonyDiv);
        Customer peter = new Customer(3L, "Peter", "Griffin", "31 Spooner St", "02907", "(123)456-7890", null, null, peterDiv);
        Customer sherlock = new Customer(4L, "Sherlock", "Holmes", "221B Baker St", "NW1 6XE", "(123)456-7890", null, null, sherlockDiv);
        Customer frasier = new Customer(5L, "Frasier", "Crane", "Apartment 1901, Elliott Bay Towers", "98101", "(123)456-7890", null, null, frasierDiv);
        Customer poirot = new Customer(6L, "Hercule", "Poirot", "Apt. 56B, Whitehaven Mansions", "EC2Y 5HN", "(123)456-7890", null, null, poirotDiv);

        customerRepository.saveAll(Arrays.asList(customerOne, tony, peter, sherlock, frasier, poirot));

        System.out.println("Sample data populated successfully");
    }
}
