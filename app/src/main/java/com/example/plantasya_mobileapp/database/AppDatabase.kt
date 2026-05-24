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
    version = 9,
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
                if (libraryPlantDao.getCount() > 0) return

                val plants = listOf(
                    LibraryPlant(
                        plantName = "Aglaonema",
                        scientificName = "Aglaonema commutatum",
                        description = "Evergreen perennial prized for its large, glossy leaves.",
                        lightPt = "Low to moderate, indirect light.",
                        waterPt = "Keep soil moist; water when top 1-2 inches of soil is dry.",
                        humidityPlt = "Tolerates low humidity.",
                        tempPlt = "68-80°F (20-27°C).",
                        soilPlt = "Well-draining mix.",
                        fertilizerPlt = "Feed monthly in spring/summer with half-strength balanced liquid fertilizer.",
                        toxicityPlt = "Toxic to dogs and cats.",
                        plantVariation = "'Emerald Beauty', 'Red Siam'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_aglonema)
                    ),
                    LibraryPlant(
                        plantName = "Snake Plant",
                        scientificName = "Dracaena trifasciata",
                        description = "Stemless, erect, succulent perennial with sword-like leaves.",
                        lightPt = "Bright, indirect light.",
                        waterPt = "Low; allow soil to dry completely.",
                        humidityPlt = "Low to moderate.",
                        tempPlt = "60-75°F (16-24°C).",
                        soilPlt = "Well-draining succulent mix.",
                        fertilizerPlt = "Fertilize once a month in spring/summer with diluted cactus or balanced fertilizer.",
                        toxicityPlt = "Toxic to pets.",
                        plantVariation = "'Laurentii', 'Silver Hahnii'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_snakeplant)
                    ),
                    LibraryPlant(
                        plantName = "Philodendron",
                        scientificName = "Philodendron genus",
                        description = "Vining or upright herbs with heart-shaped leaves.",
                        lightPt = "Medium to bright, indirect light.",
                        waterPt = "Moderate; water when top 2-3 inches dry.",
                        humidityPlt = "Medium to high.",
                        tempPlt = "65-85°F (18-29°C).",
                        soilPlt = "Light, loamy mix.",
                        fertilizerPlt = "Feed monthly in spring/summer with half-strength balanced liquid fertilizer.",
                        toxicityPlt = "Toxic if consumed.",
                        plantVariation = "Heartleaf, 'Brazil'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_philodendron)
                    ),
                    LibraryPlant(
                        plantName = "Calathea",
                        scientificName = "Calathea spp.",
                        description = "Low-growing perennial with patterned leaves.",
                        lightPt = "Low to moderate, indirect light.",
                        waterPt = "Keep soil consistently moist.",
                        humidityPlt = "Needs high humidity.",
                        tempPlt = "65-85°F (18-29°C).",
                        soilPlt = "Well-draining, acidic mix.",
                        fertilizerPlt = "Fertilize once a month in spring/summer with diluted balanced fertilizer (10-10-10).",
                        toxicityPlt = "Nontoxic.",
                        plantVariation = "Rattlesnake Plant, 'Medallion'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_calathea)
                    ),
                    LibraryPlant(
                        plantName = "Bromeliad",
                        scientificName = "Family Bromeliaceae",
                        description = "Herbaceous perennials with rosette foliage.",
                        lightPt = "Partial sun; bright, indirect light.",
                        waterPt = "Low; water into the central cup.",
                        humidityPlt = "Prefers high humidity.",
                        tempPlt = "65-80°F (18-27°C).",
                        soilPlt = "Fast-draining mix.",
                        fertilizerPlt = "Use diluted balanced fertilizer as a foliar spray once a month in spring/summer.",
                        toxicityPlt = "Nontoxic.",
                        plantVariation = "Flaming Sword, Queen's Tears.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_bromeliad)
                    ),
                    LibraryPlant(
                        plantName = "Peace Lily",
                        scientificName = "Spathiphyllum spp.",
                        description = "Dark green leaves and white spathe flowers.",
                        lightPt = "Bright indirect; tolerates low light.",
                        waterPt = "Keep soil lightly moist.",
                        humidityPlt = "Prefers moderate humidity.",
                        tempPlt = "68-85°F (20-29°C).",
                        soilPlt = "Standard potting mix.",
                        fertilizerPlt = "Feed only 2–3 times in spring/summer with half-strength balanced fertilizer.",
                        toxicityPlt = "Mildly toxic.",
                        plantVariation = "'Sensation', 'Domino'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_peachlily)
                    ),
                    LibraryPlant(
                        plantName = "Rubber Tree",
                        scientificName = "Ficus elastica",
                        description = "Broadleaf evergreen tree with thick, leathery leaves.",
                        lightPt = "Bright, indirect light.",
                        waterPt = "Once per week.",
                        humidityPlt = "High humidity.",
                        tempPlt = "65-85°F (18-29°C).",
                        soilPlt = "Well-draining soil.",
                        fertilizerPlt = "Fertilize every 2–4 weeks in spring/summer with half-strength balanced fertilizer.",
                        toxicityPlt = "Toxic (irritating).",
                        plantVariation = "'Robusta', 'Burgundy'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_rubbertree)
                    ),
                    LibraryPlant(
                        plantName = "Fiddle Leaf Fig",
                        scientificName = "Ficus lyrata",
                        description = "Tropical tree with lyre-shaped leaves.",
                        lightPt = "Bright, indirect light.",
                        waterPt = "Water when top few inches are dry.",
                        humidityPlt = "Prefers medium humidity.",
                        tempPlt = ">55°F (13°C).",
                        soilPlt = "Moist, loamy, acidic soil.",
                        fertilizerPlt = "Feed every 2–4 weeks in spring/summer with a 3-1-2 or balanced fertilizer.",
                        toxicityPlt = "Toxic.",
                        plantVariation = "Variegated Fiddle Leaf Fig.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_fiddleleaf)
                    ),
                    LibraryPlant(
                        plantName = "Orchid",
                        scientificName = "Phalaenopsis",
                        description = "Epiphyte with waxy, long-lasting flowers.",
                        lightPt = "Bright, indirect.",
                        waterPt = "Weekly; let roots dry slightly.",
                        humidityPlt = "50–70%.",
                        tempPlt = "65–80°F.",
                        soilPlt = "Chunky orchid bark.",
                        fertilizerPlt = "Use orchid-specific fertilizer every 4–6 weeks in spring/summer.",
                        toxicityPlt = "Non-toxic.",
                        plantVariation = "Dendrobium, Cattleya.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_orchids)
                    ),
                    LibraryPlant(
                        plantName = "Spider Plant",
                        scientificName = "Chlorophytum comosum",
                        description = "Arching, narrow leaves; produces spiderettes.",
                        lightPt = "Bright, indirect.",
                        waterPt = "When top 1 inch dry.",
                        humidityPlt = "Average.",
                        tempPlt = "65–75°F.",
                        soilPlt = "All-purpose mix.",
                        fertilizerPlt = "Feed monthly in spring/summer with half-strength all-purpose fertilizer.",
                        toxicityPlt = "Non-toxic.",
                        plantVariation = "'Vittatum', 'Variegatum'.",
                        coverPhoto = BitmapConverter.drawableToByteArray(context, R.drawable.cover_spiderplants)
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
