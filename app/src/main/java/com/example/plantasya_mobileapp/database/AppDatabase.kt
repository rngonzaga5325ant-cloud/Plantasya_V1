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
                if (libraryPlantDao.getCount() > 0) {
                    // Force update photos even if library is not empty (optional, but good for this task)
                    // libraryPlantDao.clearAll() 
                    // or just return if we don't want to re-populate every time
                    return 
                }

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
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_dracaena)
                    )
                )
                
                plants.forEach { libraryPlantDao.insert(it) }
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
