package com.example.plantasya_mobileapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.plantasya_mobileapp.BitmapConverter
import com.example.plantasya_mobileapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(
    entities = [
        User::class,
        OwnedPlant::class,
        Task::class,
        LibraryPlant::class,
        History::class,
        UserSessionRecord::class
    ],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun ownedPlantDao(): OwnedPlantDao
    abstract fun taskDao(): TaskDao
    abstract fun libraryPlantDao(): LibraryPlantDao
    abstract fun historyDao(): HistoryDao
    abstract fun sessionDao(): UserSessionRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plantasya_db"
                )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val userDao = database.userDao()
                        val libraryDao = database.libraryPlantDao()
                        
                        if (userDao.getUserByUsername("admin") == null) {
                            populateDatabase(userDao)
                        }
                        
                        populateLibraryIfEmpty(libraryDao)
                    }
                }
            }

            suspend fun populateDatabase(userDao: UserDao) {
                val adminUser = User(
                    username = "admin",
                    password = hashPassword("adminplantasya"),
                    role = "admin"
                )
                userDao.insertUser(adminUser)
            }

            suspend fun populateLibraryIfEmpty(libraryPlantDao: LibraryPlantDao) {
                val plants = listOf(
                    LibraryPlant(
                        plantName = "Aglaonema",
                        scientificName = "Aglaonema commutatum",
                        description = "Attractive evergreen with glossy patterned leaves; easy indoor plant.",
                        lightPt = "Low to bright indirect light; avoid direct sun.",
                        waterPt = "Allow top 1-2 inches of soil to dry between waterings.",
                        humidityPlt = "Moderate humidity preferred.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Well-draining potting mix; add perlite for aeration.",
                        fertilizerPlt = "Monthly during growing season with half-strength balanced fertilizer.",
                        toxicityPlt = "Toxic to pets if ingested.",
                        plantVariation = "'Emerald', 'Silver Queen'",
                        heightRange = "1–3 ft (30–90 cm)",
                        spaceOccupancy = "Compact; suitable for tabletops and shelves (footprint ~1–2 ft)",
                        plantUse = "Indoor decor on tables, shelves, or floor potted; not recommended for edible/consumable use",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_aglonema),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_aglonema)
                    ),
                    LibraryPlant(
                        plantName = "Pothos",
                        scientificName = "Epipremnum aureum",
                        description = "Vigorous trailing vine with heart-shaped leaves; very forgiving.",
                        lightPt = "Low to bright indirect light; tolerates low light well.",
                        waterPt = "Water when top 2 inches dry; tolerate occasional missed waterings.",
                        humidityPlt = "Average home humidity.",
                        tempPlt = "65-85°F (18-29°C)",
                        soilPlt = "Well-draining all-purpose potting mix.",
                        fertilizerPlt = "Feed monthly in spring/summer with balanced liquid fertilizer.",
                        toxicityPlt = "Toxic to pets.",
                        plantVariation = "Golden, Marble Queen, Neon",
                        heightRange = "Trailing up to 6–10 ft (180–300 cm)",
                        spaceOccupancy = "Great for hanging baskets, shelves, or climbing; minimal floor footprint",
                        plantUse = "Hanging baskets, shelves, or trained on supports; not for consumption",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_pothos),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_pothos)
                    ),
                    LibraryPlant(
                        plantName = "Calathea",
                        scientificName = "Calathea spp.",
                        description = "Decorative foliage plant known for patterned leaves and daily leaf movement.",
                        lightPt = "Low to medium indirect light; avoid direct sun.",
                        waterPt = "Keep soil evenly moist but not soggy; use filtered or non-hard water if possible.",
                        humidityPlt = "High humidity recommended.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Well-draining, slightly acidic potting mix rich in organic matter.",
                        fertilizerPlt = "Monthly with diluted balanced fertilizer during growing season.",
                        toxicityPlt = "Non-toxic to pets.",
                        plantVariation = "Medallion, Rattlesnake",
                        heightRange = "1–2 ft (30–60 cm)",
                        spaceOccupancy = "Low-growing; good for tabletops, shelves, or small floor pots",
                        plantUse = "Indoor decorative plant; good for terrariums or grouped displays",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_calathea),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_calathea)
                    ),
                    LibraryPlant(
                        plantName = "Money Tree",
                        scientificName = "Pachira aquatica",
                        description = "Popular braided-trunk tree symbolizing good luck; adaptable indoor tree.",
                        lightPt = "Bright, indirect light; tolerates some lower light.",
                        waterPt = "Water when top 2-3 inches dry; avoid waterlogging.",
                        humidityPlt = "Prefers moderate humidity.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Well-draining potting mix with good moisture retention.",
                        fertilizerPlt = "Feed every 4–6 weeks in spring/summer with balanced fertilizer.",
                        toxicityPlt = "Generally considered non-toxic but check varieties.",
                        plantVariation = "Standard braided forms",
                        heightRange = "3–6 ft (90–180 cm) indoors",
                        spaceOccupancy = "Medium to large floor plant; needs 2–4 ft clearance",
                        plantUse = "Floor specimen or office corner plant; not for food use",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_moneytree),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_moneytree)
                    ),
                    LibraryPlant(
                        plantName = "Orchid",
                        scientificName = "Phalaenopsis",
                        description = "Epiphytic orchid prized for long-lasting, showy blooms.",
                        lightPt = "Bright, indirect light; east or west windows ideal.",
                        waterPt = "Water every 7–10 days; let medium dry slightly between waterings.",
                        humidityPlt = "50–70% humidity preferred.",
                        tempPlt = "60-80°F (16-27°C)",
                        soilPlt = "Open, chunky orchid bark or sphagnum mix.",
                        fertilizerPlt = "Use orchid fertilizer every 2–4 weeks during active growth.",
                        toxicityPlt = "Non-toxic.",
                        plantVariation = "Phalaenopsis, Dendrobium",
                        heightRange = "8–24 in (20–60 cm) including flower spike",
                        spaceOccupancy = "Compact; ideal for tabletops, shelves, windowsills",
                        plantUse = "Tabletop or shelf display; excellent for decorative vases or stands",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_orchid),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_orchids)
                    ),
                    LibraryPlant(
                        plantName = "Parlor Palm",
                        scientificName = "Chamaedorea elegans",
                        description = "Graceful, slow-growing palm suited for low-light interiors.",
                        lightPt = "Low to medium indirect light.",
                        waterPt = "Keep evenly moist; allow slight drying between waterings.",
                        humidityPlt = "Prefers moderate humidity.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Well-draining, organic-rich potting soil.",
                        fertilizerPlt = "Light feeding in spring/summer with balanced fertilizer.",
                        toxicityPlt = "Non-toxic to pets.",
                        plantVariation = "Standard",
                        heightRange = "2–6 ft (60–180 cm) indoors",
                        spaceOccupancy = "Medium floor plant; suitable for corners or beside furniture",
                        plantUse = "Floor specimen, office plant, or grouped in planters; not edible",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_parlor_palm),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_parlorpalm)
                    ),
                    LibraryPlant(
                        plantName = "ZZ Plant",
                        scientificName = "Zamioculcas zamiifolia",
                        description = "Very low-maintenance plant with glossy, upright leaflets.",
                        lightPt = "Low to bright indirect light; tolerates low light exceptionally well.",
                        waterPt = "Very drought-tolerant; water sparingly and let soil dry between waterings.",
                        humidityPlt = "Average home humidity.",
                        tempPlt = "65-85°F (18-29°C)",
                        soilPlt = "Well-draining potting mix; add perlite or coarse sand.",
                        fertilizerPlt = "Light feeding once every 6–8 weeks in growing season.",
                        toxicityPlt = "Toxic if ingested.",
                        plantVariation = "Standard, 'Raven'",
                        heightRange = "1–3 ft (30–90 cm)",
                        spaceOccupancy = "Compact upright form; fits tabletops or small floor pots",
                        plantUse = "Desk, shelf, or floor plant; excellent for low-light corners",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_zz),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_zzplant)
                    ),
                    LibraryPlant(
                        plantName = "Peace Lily",
                        scientificName = "Spathiphyllum spp.",
                        description = "Popular flowering houseplant with glossy leaves and white spathes.",
                        lightPt = "Low to bright indirect light; avoid direct sun.",
                        waterPt = "Keep soil lightly moist; wilts when thirsty then recovers after watering.",
                        humidityPlt = "Prefers moderate to high humidity.",
                        tempPlt = "65-85°F (18-29°C)",
                        soilPlt = "Well-draining, rich potting soil.",
                        fertilizerPlt = "Light feeding every 4–6 weeks during growth.",
                        toxicityPlt = "Mildly toxic to pets.",
                        plantVariation = "'Sensation', 'Domino'",
                        heightRange = "1–4 ft (30–120 cm)",
                        spaceOccupancy = "Small to medium floor plant; can be placed on tables when young",
                        plantUse = "Floor or tabletop accent; also used for indoor air purification displays",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_peachlily),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_peachlily)
                    ),
                    LibraryPlant(
                        plantName = "Snake Plant",
                        scientificName = "Dracaena trifasciata",
                        description = "Architectural, upright leaves; extremely low-maintenance.",
                        lightPt = "Low to bright indirect light; tolerates sun.",
                        waterPt = "Allow soil to dry completely between waterings.",
                        humidityPlt = "Low to moderate humidity.",
                        tempPlt = "60-85°F (16-29°C)",
                        soilPlt = "Well-draining succulent/cactus mix.",
                        fertilizerPlt = "Light feeding in spring/summer with diluted fertilizer.",
                        toxicityPlt = "Toxic to pets.",
                        plantVariation = "'Laurentii', 'Silver Hahnii'",
                        heightRange = "1–4 ft (30–120 cm)",
                        spaceOccupancy = "Narrow, upright footprint; ideal for tight spaces and corners",
                        plantUse = "Floor or shelf (tall narrow pots); good for entryways and offices",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_snakeplant),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_snakeplant)
                    ),
                    LibraryPlant(
                        plantName = "Dracaena",
                        scientificName = "Dracaena spp.",
                        description = "Diverse genus from small shrubs to large indoor trees with strap-like leaves.",
                        lightPt = "Medium to bright indirect light; some varieties tolerate lower light.",
                        waterPt = "Water when top 1-2 inches dry; avoid waterlogged soil.",
                        humidityPlt = "Prefers moderate humidity.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Well-draining potting mix with organic matter.",
                        fertilizerPlt = "Feed monthly in growing season with balanced fertilizer.",
                        toxicityPlt = "Some species toxic to pets.",
                        plantVariation = "Corn plant, Marginata",
                        heightRange = "2–10 ft (60–300 cm) depending on species",
                        spaceOccupancy = "Small to large floor plant; choose variety based on space",
                        plantUse = "Floor specimen, office or lobby plants; not for consumption",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_dracaena),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_dracaena)
                    ),
                    LibraryPlant(
                        plantName = "Cast Iron Plant",
                        scientificName = "Aspidistra elatior",
                        description = "Extremely tough houseplant with deep green, leathery leaves that thrives on neglect.",
                        lightPt = "Low to bright indirect light; tolerates deep shade.",
                        waterPt = "Allow soil to dry almost completely between waterings.",
                        humidityPlt = "Tolerates dry air; average home humidity.",
                        tempPlt = "50-75°F (10-24°C)",
                        soilPlt = "Well-draining, rich potting soil mix.",
                        fertilizerPlt = "Feed once a month in spring/summer with liquid fertilizer.",
                        toxicityPlt = "Non-toxic to cats and dogs.",
                        plantVariation = "'Milky Way', 'Variegata'",
                        heightRange = "2–3 ft (60–90 cm)",
                        spaceOccupancy = "Upright clump form; great for floor pots or corners",
                        plantUse = "Floor accent plant or entryway decor; non-edible",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_cast_iron),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_cast_iron)
                    ),
                    LibraryPlant(
                        plantName = "Chrysanthemum",
                        scientificName = "Chrysanthemum morifolium",
                        description = "Popular flowering plant with vibrant, dense blooms in various colors.",
                        lightPt = "Full sun to bright direct light indoors.",
                        waterPt = "Keep soil evenly moist; do not allow to dry out completely.",
                        humidityPlt = "Moderate humidity preferred.",
                        tempPlt = "60-70°F (15-21°C)",
                        soilPlt = "Fertile, well-draining moist potting mix.",
                        fertilizerPlt = "Feed every 2 weeks during flowering season.",
                        toxicityPlt = "Toxic to dogs, cats, and horses.",
                        plantVariation = "'Rainbow', 'Spider', 'Pompon'",
                        heightRange = "1–3 ft (30–90 cm)",
                        spaceOccupancy = "Compact bushy plant; suitable for windowsills or patio tables",
                        plantUse = "Decorative seasonal flowering plant; tea production (specific varieties)",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.cover_chrysantemum),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_chrysantemum)
                    ),
                    LibraryPlant(
                        plantName = "Ctenanthe",
                        scientificName = "Ctenanthe oppenheimiana",
                        description = "Striking prayer-plant relative with silver and green patterned foliage.",
                        lightPt = "Medium to bright indirect light; avoid direct sun.",
                        waterPt = "Keep soil moist; use purified or filtered water.",
                        humidityPlt = "High humidity (>60%) required.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Peat-based, well-draining potting soil.",
                        fertilizerPlt = "Monthly during growing season with diluted fertilizer.",
                        toxicityPlt = "Non-toxic to pets.",
                        plantVariation = "'Tricolor', 'Burle-Marxii'",
                        heightRange = "2–3 ft (60–90 cm)",
                        spaceOccupancy = "Bushy spreading habit; ideal for tabletops and plant stands",
                        plantUse = "Indoor ornamental foliage plant; non-edible",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_cthenathe),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_cthenathe)
                    ),
                    LibraryPlant(
                        plantName = "English Ivy",
                        scientificName = "Hedera helix",
                        description = "Classic evergreen trailing vine with distinctive lobed leaves.",
                        lightPt = "Medium to bright indirect light; cool sun tolerated.",
                        waterPt = "Water when top inch of soil feels dry.",
                        humidityPlt = "Moderate to high humidity; appreciates misting.",
                        tempPlt = "50-70°F (10-21°C)",
                        soilPlt = "Standard, well-draining potting soil.",
                        fertilizerPlt = "Feed monthly spring through autumn.",
                        toxicityPlt = "Toxic to pets if ingested.",
                        plantVariation = "'Glacier', 'Needlepoint', 'Goldheart'",
                        heightRange = "Trailing up to 6–10 ft (180–300 cm)",
                        spaceOccupancy = "Hanging or climbing vine; minimal horizontal floor space",
                        plantUse = "Hanging baskets, topiary frames, or shelf cascading plant",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_englishivy),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_english_iv)
                    ),
                    LibraryPlant(
                        plantName = "Dumb Cane",
                        scientificName = "Dieffenbachia spp.",
                        description = "Broad-leaved tropical plant with beautiful cream and green variegated foliage.",
                        lightPt = "Filtered light to medium indirect shade.",
                        waterPt = "Water thoroughly then allow top 2 inches to dry.",
                        humidityPlt = "Moderate to high humidity.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Rich, aerated potting soil with perlite.",
                        fertilizerPlt = "Feed bi-weekly in spring and summer.",
                        toxicityPlt = "Highly toxic to humans and pets if chewed.",
                        plantVariation = "'Camille', 'Tropic Snow', 'Compacta'",
                        heightRange = "3–6 ft (90–180 cm)",
                        spaceOccupancy = "Large upright foliage; needs floor or pedestal space",
                        plantUse = "Indoor floor accent; keep away from children and pets",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_dumbcane),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_dumbcane)
                    ),
                    LibraryPlant(
                        plantName = "Hyacinth",
                        scientificName = "Hyacinthus orientalis",
                        description = "Spring bulb plant famous for dense clusters of intensely fragrant blossoms.",
                        lightPt = "Bright light with some direct sunlight.",
                        waterPt = "Keep soil consistently moist while blooming.",
                        humidityPlt = "Average indoor humidity.",
                        tempPlt = "45-65°F (7-18°C)",
                        soilPlt = "Well-draining bulb potting mix.",
                        fertilizerPlt = "Fertilize once after blooming if forced.",
                        toxicityPlt = "Toxic to dogs and cats (especially bulbs).",
                        plantVariation = "'Blue Jacket', 'Jan Bos', 'City of Haarlem'",
                        heightRange = "8–12 in (20–30 cm)",
                        spaceOccupancy = "Compact pot size; fits windowsills and small tables",
                        plantUse = "Seasonal flowering centerpieces and indoor fragrance display",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_hyacinth),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_hyacinth)
                    ),
                    LibraryPlant(
                        plantName = "Jade Plant",
                        scientificName = "Crassula ovata",
                        description = "Classic tree-like succulent with thick, fleshy green leaves and woody stems.",
                        lightPt = "Bright light to direct sunlight for 4+ hours daily.",
                        waterPt = "Water deeply only when soil is completely dry.",
                        humidityPlt = "Low humidity; tolerates dry indoor air.",
                        tempPlt = "65-75°F (18-24°C)",
                        soilPlt = "Cactus or succulent fast-draining gritty mix.",
                        fertilizerPlt = "Feed every 2 months in growing season.",
                        toxicityPlt = "Toxic to pets.",
                        plantVariation = "'Gollum', 'Tricolor', 'Sunset'",
                        heightRange = "2–4 ft (60–120 cm) indoors",
                        spaceOccupancy = "Slow-growing bushy tree; fits desks when young, floor when mature",
                        plantUse = "Feng Shui good luck plant, tabletop or windowsill decoration",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_jadeplant),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_jadeplant)
                    ),
                    LibraryPlant(
                        plantName = "Kalanchoe",
                        scientificName = "Kalanchoe blossfeldiana",
                        description = "Charming succulent with scalloped dark green leaves and clusters of long-lasting flowers.",
                        lightPt = "Bright, indirect light to full sun.",
                        waterPt = "Water when top 2 inches of soil feel dry.",
                        humidityPlt = "Low to average home humidity.",
                        tempPlt = "60-75°F (15-24°C)",
                        soilPlt = "Light, well-draining succulent potting mix.",
                        fertilizerPlt = "Monthly during active growth with balanced liquid feed.",
                        toxicityPlt = "Toxic to pets.",
                        plantVariation = "'Calandiva', 'Flapjack', 'Panda Plant'",
                        heightRange = "6–18 in (15–45 cm)",
                        spaceOccupancy = "Compact footprint; ideal for desks and kitchen counters",
                        plantUse = "Flowering indoor centerpiece or window plant",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.cover_kalanchoe),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_kalanchoe)
                    ),
                    LibraryPlant(
                        plantName = "Monstera Deliciosa",
                        scientificName = "Monstera deliciosa",
                        description = "Iconic tropical plant with large, glossy leaves featured with dramatic split fenestrations.",
                        lightPt = "Bright, indirect light; tolerates medium light.",
                        waterPt = "Water every 1-2 weeks; let soil dry between waterings.",
                        humidityPlt = "Prefers high humidity (>50%).",
                        tempPlt = "65-85°F (18-29°C)",
                        soilPlt = "Peat-based mix with perlite and orchid bark.",
                        fertilizerPlt = "Feed monthly during spring/summer.",
                        toxicityPlt = "Toxic to pets if chewed.",
                        plantVariation = "'Thai Constellation', 'Albo Variegata'",
                        heightRange = "3–8 ft (90–240 cm) indoors",
                        spaceOccupancy = "Large statement plant; requires wide floor space and moss pole",
                        plantUse = "Corner floor display and living room focal point",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_monstera),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_monstera)
                    ),
                    LibraryPlant(
                        plantName = "Poinsettia",
                        scientificName = "Euphorbia pulcherrima",
                        description = "Famous holiday plant known for its vibrant red leaf bracts surrounding tiny yellow flowers.",
                        lightPt = "Bright indirect light; avoid harsh direct sun.",
                        waterPt = "Water when soil surface feels dry to touch.",
                        humidityPlt = "Moderate humidity; keep away from dry heater drafts.",
                        tempPlt = "65-70°F (18-21°C)",
                        soilPlt = "Peat-based lightweight potting mixture.",
                        fertilizerPlt = "Not needed during bloom; feed monthly after holidays.",
                        toxicityPlt = "Mildly toxic to pets (causes mild mouth/stomach irritation).",
                        plantVariation = "'Red', 'Pink', 'White', 'Marbled'",
                        heightRange = "1–3 ft (30–90 cm)",
                        spaceOccupancy = "Medium bushy habit; good for mantlepieces or dining tables",
                        plantUse = "Seasonal indoor Christmas decoration",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_poinsettia),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_poinsettia)
                    ),
                    LibraryPlant(
                        plantName = "Polka Dot Plant",
                        scientificName = "Hypoestes phyllostachya",
                        description = "Colorful small plant featuring heavily spotted pink, red, or white splash patterns.",
                        lightPt = "Bright indirect light to maintain vivid leaf spots.",
                        waterPt = "Keep soil consistently moist, not waterlogged.",
                        humidityPlt = "High humidity preferred.",
                        tempPlt = "65-75°F (18-24°C)",
                        soilPlt = "Organic-rich, well-draining soil.",
                        fertilizerPlt = "Feed monthly in spring and summer.",
                        toxicityPlt = "Non-toxic to pets.",
                        plantVariation = "'Splash Select Pink', 'Red Splash'",
                        heightRange = "6–12 in (15–30 cm)",
                        spaceOccupancy = "Small compact foliage; ideal for terrariums and desks",
                        plantUse = "Desktop accent, shelf filler, or terrarium garden plant",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_polkadotplant),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_polkadotplant)
                    ),
                    LibraryPlant(
                        plantName = "Pony Tail Palm",
                        scientificName = "Beaucarnea recurvata",
                        description = "Unique succulent tree with a bulbous swollen trunk and cascading ribbon-like leaves.",
                        lightPt = "Bright indirect to direct sunlight.",
                        waterPt = "Store water in trunk; water sparingly every 2-3 weeks.",
                        humidityPlt = "Tolerates dry indoor humidity.",
                        tempPlt = "60-80°F (16-27°C)",
                        soilPlt = "Fast-draining cactus/succulent soil.",
                        fertilizerPlt = "Feed 2-3 times during spring/summer.",
                        toxicityPlt = "Non-toxic to cats and dogs.",
                        plantVariation = "'Standard'",
                        heightRange = "2–6 ft (60–180 cm) indoors",
                        spaceOccupancy = "Swollen base footprint; desktop when young, floor when mature",
                        plantUse = "Architectural feature plant for sunny room corners",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_ponytailpalm),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_ponytailpalm)
                    ),
                    LibraryPlant(
                        plantName = "Prayer Plant",
                        scientificName = "Maranta leuconeura",
                        description = "Charming tropical foliage that folds its leaves upward at night like hands in prayer.",
                        lightPt = "Medium indirect light; sensitive to bright sunlight.",
                        waterPt = "Keep soil consistently damp using lukewarm water.",
                        humidityPlt = "High humidity required.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Peat-rich, well-draining potting mix.",
                        fertilizerPlt = "Feed every 2 weeks spring through autumn.",
                        toxicityPlt = "Non-toxic to pets.",
                        plantVariation = "'Tricolor', 'Red Vein', 'Green Vein'",
                        heightRange = "6–12 in (15–30 cm)",
                        spaceOccupancy = "Low spreading growth; perfect for plant stands and tables",
                        plantUse = "Indoor ornamental table plant or hanging display",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_prayerplant),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_prayerplant)
                    ),
                    LibraryPlant(
                        plantName = "Rubber Plant",
                        scientificName = "Ficus elastica",
                        description = "Bold indoor tree with thick, glossy dark burgundy-green leathery leaves.",
                        lightPt = "Bright indirect light; tolerates morning sun.",
                        waterPt = "Water when top 2 inches of soil are dry.",
                        humidityPlt = "Moderate indoor humidity.",
                        tempPlt = "65-78°F (18-25°C)",
                        soilPlt = "Well-aerated potting mix with perlite.",
                        fertilizerPlt = "Feed monthly during active spring/summer growth.",
                        toxicityPlt = "Toxic to dogs and cats (sap causes skin/stomach irritation).",
                        plantVariation = "'Burgundy', 'Tineke', 'Ruby'",
                        heightRange = "3–10 ft (90–300 cm) indoors",
                        spaceOccupancy = "Tall vertical growth; requires floor placement",
                        plantUse = "Statement floor plant for living rooms or grand entryways",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_rubberplant),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_rubberplant)
                    ),
                    LibraryPlant(
                        plantName = "Schefflera",
                        scientificName = "Schefflera arboricola",
                        description = "Popular umbrella plant with shiny leaves growing in starburst umbrella clusters.",
                        lightPt = "Bright, indirect light; tolerates medium light.",
                        waterPt = "Water when top soil layer dries out; avoid overwatering.",
                        humidityPlt = "Moderate to high humidity.",
                        tempPlt = "65-80°F (18-27°C)",
                        soilPlt = "Rich, moist but well-draining soil.",
                        fertilizerPlt = "Feed monthly spring through summer.",
                        toxicityPlt = "Toxic to pets.",
                        plantVariation = "'Gold Capella', 'Janine'",
                        heightRange = "3–6 ft (90–180 cm)",
                        spaceOccupancy = "Bushy upright canopy; fits floor spaces and corners",
                        plantUse = "Floor decor plant or indoor tree display",
                        plantPic = BitmapConverter.drawableToByteArray(context, R.drawable.profile_schefflera),
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_schefflera)
                    )
                )

                plants.forEach { plant ->
                    plant.plantName?.let { name ->
                        val existing = libraryPlantDao.getPlantByName("%$name%")
                        if (existing == null) {
                            libraryPlantDao.insert(plant)
                        } else if (existing.plantPic == null || existing.plantPic.isEmpty()) {
                            libraryPlantDao.updatePlantPic(name, plant.plantPic)
                        }
                    }
                }
            }

            private fun hashPassword(password: String): String {
                val bytes = password.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                return digest.fold("") { str, it -> str + "%02x".format(it) }
            }
        }
    }
}
