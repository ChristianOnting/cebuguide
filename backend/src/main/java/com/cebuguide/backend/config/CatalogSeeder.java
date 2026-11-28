package com.cebuguide.backend.config;

import com.cebuguide.backend.model.Category;
import com.cebuguide.backend.model.Spot;
import com.cebuguide.backend.repository.CategoryRepository;
import com.cebuguide.backend.repository.SpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Loads starter categories and well-known Cebu spots the first time the database is empty, so the
 * directory and the itinerary planner have real data to work with. The coordinates are approximate;
 * an administrator can correct them from the Spots page.
 */
@Component
public class CatalogSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogSeeder.class);

    private final CategoryRepository categories;
    private final SpotRepository spots;

    public CatalogSeeder(CategoryRepository categories, SpotRepository spots) {
        this.categories = categories;
        this.spots = spots;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (categories.count() == 0) {
            categories.saveAll(List.of(
                    new Category("Historical and Heritage", "#8e44ad"),
                    new Category("Religious Sites", "#c0392b"),
                    new Category("Beaches and Islands", "#1e90ff"),
                    new Category("Waterfalls and Nature", "#27ae60"),
                    new Category("Museums and Culture", "#d35400"),
                    new Category("Food and Markets", "#e6a700")));
            log.info("Starter categories created");
        }
        if (spots.count() == 0) {
            Map<String, Category> byName = new java.util.HashMap<>();
            categories.findAll().forEach(c -> byName.put(c.getName(), c));
            add(byName, "Magellan's Cross", "Historical and Heritage", "Magallanes Street, Cebu City", "10.2935", "123.9021", false,
                    "A cross planted in Cebu in 1521 to mark the arrival of Ferdinand Magellan and the start of Christianity in the country. It stands in a small chapel across from the Basilica.");
            add(byName, "Basilica Minore del Santo Nino", "Religious Sites", "Pilgrim's Center, Osmena Boulevard, Cebu City", "10.2942", "123.9021", true,
                    "The oldest Roman Catholic church in the Philippines, home of the Santo Nino de Cebu image and the center of the Sinulog festival devotion.");
            add(byName, "Fort San Pedro", "Historical and Heritage", "A. Pigafetta Street, Cebu City", "10.2925", "123.9055", false,
                    "A 17th-century Spanish military fortress beside the harbor. It is the oldest triangular bastion fort in the country and is open to walk through.");
            add(byName, "Cebu Taoist Temple", "Religious Sites", "Beverly Hills, Cebu City", "10.3376", "123.8864", false,
                    "A Chinese temple on a hillside in Beverly Hills with views over the city. Visitors of any faith are welcome to look around.");
            add(byName, "Casa Gorordo Museum", "Museums and Culture", "Lopez Jaena Street, Cebu City", "10.2951", "123.9003", true,
                    "A restored 19th-century house in the old city that shows how a well-to-do Cebuano family lived, with period furniture and displays.");
            add(byName, "Carbon Market", "Food and Markets", "M. C. Briones Street, Cebu City", "10.2937", "123.8975", true,
                    "A large traditional public market selling fresh produce, seafood, dried fish, and local goods. A good place to see everyday Cebuano life.");
            add(byName, "Mactan Shrine", "Historical and Heritage", "Punta Engano Road, Lapu-Lapu City", "10.3107", "123.9793", false,
                    "A memorial to Lapulapu and the Battle of Mactan in 1521, with a large statue of the chieftain in Lapu-Lapu City.");
            add(byName, "Kawasan Falls", "Waterfalls and Nature", "Barangay Matutinao, Badian", "9.8029", "123.3762", false,
                    "A multi-tiered waterfall in Badian known for its turquoise water. It is a popular stop for swimming and for canyoneering tours.");
            add(byName, "Panagsama Beach, Moalboal", "Beaches and Islands", "Panagsama, Moalboal", "9.9437", "123.3715", false,
                    "A beach strip in Moalboal that is popular for diving and snorkeling, with reefs and large schools of sardines close to shore.");
            add(byName, "Malapascua Island", "Beaches and Islands", "Daanbantayan", "11.3333", "124.1167", false,
                    "A small island off the northern tip of Cebu, known for white sand beaches and for diving where thresher sharks can be seen.");
            add(byName, "Bantayan Island", "Beaches and Islands", "Bantayan", "11.1700", "123.7200", false,
                    "An island off northwest Cebu known for long white sand beaches, quiet villages, and seafood. It is reached by ferry from Hagnaya.");
            log.info("Starter spots created");
        }
    }

    private void add(Map<String, Category> byName, String name, String category, String address,
                     String lat, String lon, boolean indoor, String description) {
        Spot s = new Spot();
        s.setName(name);
        s.setCategory(byName.get(category));
        s.setAddress(address);
        s.setLatitude(new BigDecimal(lat));
        s.setLongitude(new BigDecimal(lon));
        s.setIndoor(indoor);
        s.setDescription(description);
        spots.save(s);
    }
}
