import com.yaya.map.R
import support.LocaleController

class PrePaths {
    private val paths = HashMap<String, ArrayList<MutableList<String>>>()

    companion object {
        var Instanc: PrePaths? = null

        fun getInstance(): PrePaths {
            var localInstanc = Instanc
            synchronized(PrePaths::class.java) {
                localInstanc = PrePaths()
                Instanc = localInstanc
            }
            return localInstanc!!
        }

    }

    fun getPrePath(startStation: String, endStation: String): ArrayList<MutableList<String>> {
        val path1 = paths[createKey(startStation, endStation)]
        if (path1 == null) {
            val path2 = paths[createKey(endStation, startStation)]!!
            path2.forEach { list ->
                list.reverse()
            }
            return path2
        }

        return path1
    }

    private fun createKey(startStation: String, endStation: String): String {
        return String.format("%s_%s", startStation, endStation)
    }

    init {
        //tajrish-farhangsara paths
        var key = createKey(
                LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("farhangsara", R.string.farhangsara))

        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini)),

                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("shemiran", R.string.shemiran)),

                mutableListOf(
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("emam_hossein", R.string.emam_hossein)),

                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("shademan", R.string.shademan)),

                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("shademan", R.string.shademan))
        )

        //tajrish-azadegan
        key = createKey(
                LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("azadegan", R.string.azadegan))

        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti)),

                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr)),

                mutableListOf(
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("mahdiye", R.string.mahdiye))
        )

        //Tajrish_Kolahdooz
        key = createKey(
                LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))

        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat)),

                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr)),

                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("shademan", R.string.shademan))
        )

        //Tajrish_Abdolazim
        key = createKey(
                LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("abdolazim", R.string.abdolazim))

        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("haftome_tir", R.string.haftome_tir)),

                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("emam_hossein", R.string.emam_hossein)),

                mutableListOf(
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar)),

                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr)),

                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("emam_hossein", R.string.emam_hossein))
        )

        //Tajrish_Takhti
        key = createKey(LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh)),

                mutableListOf(
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares)),

                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("towhid", R.string.towhid)),

                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("navab_safavi", R.string.navab_safavi))
        )

        //Tajrish_TajrishSub
        key = createKey(LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("tajrish_sub", R.string.tajrish_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shahed", R.string.shahed))
        )

        //Tajrish_FarhangsaraSub
        key = createKey(LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("sadegheye", R.string.sadegheye))
        )

        //Tajrish_AzadeganSub
        key = createKey(LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )

        //Tajrish_KolahdoozSub
        key = createKey(LocaleController.getString("tajrish", R.string.tajrish),
                LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("bimeh", R.string.bimeh))
        )

        //Farhangsara_Azadegan
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("azadegan", R.string.azadegan))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("beheshti", R.string.beheshti)),

                mutableListOf(
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr)),

                mutableListOf(
                        LocaleController.getString("shademan", R.string.shademan),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr)),

                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr)),

                mutableListOf(
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("beheshti", R.string.beheshti)),
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr)),
                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("beheshti", R.string.beheshti))
        )

        //Farhangsara_Kolahdooz
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shemiran", R.string.shemiran)),

                mutableListOf(
                        LocaleController.getString("shademan", R.string.shademan)),

                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("dowlat", R.string.dowlat)),

                mutableListOf(
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("towhid", R.string.towhid)),

                mutableListOf(
                        LocaleController.getString("sadegheye", R.string.sadegheye),
                        LocaleController.getString("erame_sabz", R.string.erame_sabz)),
                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada))
        )

        //Farhangsara_Takhti
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("navab_safavi", R.string.navab_safavi)),

                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar)),

                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh))
        )

        //Farhangsara_Abdolazim
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("abdolazim", R.string.abdolazim))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein)),

                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("haftome_tir", R.string.haftome_tir)),

                mutableListOf(
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares))
        )

        //Farhangsara_FarhangsaraSub
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("sadegheye", R.string.sadegheye))
        )
        //Farhangsara_TajrishSub
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("tajrish_sub", R.string.tajrish_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("shahed", R.string.shahed))
        )

        // Farhangsara_AzadeganSub
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("shademan", R.string.shademan),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )

        //Farhangsara_KolahdoozSub
        key = createKey(LocaleController.getString("farhangsara", R.string.farhangsara),
                LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shademan", R.string.shademan),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("sadegheye", R.string.sadegheye),
                        LocaleController.getString("erame_sabz", R.string.erame_sabz),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("towhid", R.string.towhid),
                        LocaleController.getString("bimeh", R.string.bimeh))
        )

        //Azadegan_Kolahdooz
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("shahid_kolahdooz", R.string.shahid_kolahdooz))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr)),
                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("dowlat", R.string.dowlat))
        )

        //Azadegan_Abdolazim
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("abdolazim", R.string.abdolazim))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr)),
                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("haftome_tir", R.string.haftome_tir)),
                mutableListOf(
                        LocaleController.getString("mahdiye", R.string.mahdiye),
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar)),
                mutableListOf(
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada))
        )

        //Azadegan_Takhti
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("mahdiye", R.string.mahdiye)),
                mutableListOf(
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("towhid", R.string.towhid)),
                mutableListOf(
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares))

        )

        //Azadegan_AzadeganSub
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )

        //Azadegan_TajrishSub
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("tajrish_sub", R.string.tajrish_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("shahed", R.string.shahed))
        )

        //Azadegan_FarhangsaraSub
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("shademan", R.string.shademan),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("mahdiye", R.string.mahdiye),
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("sadegheye", R.string.sadegheye))
        )

        //Azadegan_KolahdoozSub
        key = createKey(LocaleController.getString("azadegan", R.string.azadegan),
                LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("bimeh", R.string.bimeh))
        )

        //Abdolazim_FarhangsaraSub
        key = createKey(LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                        LocaleController.getString("shademan", R.string.shemiran),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("sadegheye", R.string.sadegheye))
        )

        //Abdolazim_Takhti
        key = createKey(LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar)),
                mutableListOf(
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares)),
                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("navab_safavi", R.string.navab_safavi)),
                mutableListOf(
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("towhid", R.string.towhid)),
                mutableListOf(
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                        LocaleController.getString("towhid", R.string.towhid)),
                mutableListOf(
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("mahdiye", R.string.mahdiye)),
                mutableListOf(
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh))
        )

        //Abdolazim_TajrishSub
        key = createKey(LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("tajrish_sub", R.string.tajrish_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("shahed", R.string.shahed)),
                mutableListOf(
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar),
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("shahed", R.string.shahed)),
                mutableListOf(
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("mahdiye", R.string.mahdiye),
                        LocaleController.getString("shahed", R.string.shahed))
        )

        //Abdolazim_AzadeganSub
        key = createKey(LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )

        //Abdolazim_KolahdoozSub
        key = createKey(LocaleController.getString("abdolazim", R.string.abdolazim),
                LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares),
                        LocaleController.getString("towhid", R.string.towhid),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("bimeh", R.string.bimeh))
        )
        //Takhti_FarhangsaraSub
        key = createKey(LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),
                LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("meydane_shohada", R.string.meydane_shohada),
                        LocaleController.getString("shemiran", R.string.shemiran),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares),
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("mahdiye", R.string.mahdiye),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("shademan", R.string.shademan),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar),
                        LocaleController.getString("emam_hossein", R.string.emam_hossein),
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("sadegheye", R.string.sadegheye))
        )

        //Takhti_TajrishSub
        key = createKey(LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),
                LocaleController.getString("tajrish_sub", R.string.tajrish_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("shahed", R.string.shahed)),
                mutableListOf(
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("shahed", R.string.shahed))
        )

        //Takhti_AzadeganSub
        key = createKey(LocaleController.getString("varzeshagahe_takhti", R.string.varzeshagahe_takhti),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("tarbiat_modares", R.string.tarbiat_modares),
                        LocaleController.getString("meydane_valiasr", R.string.meydane_valiasr),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("shohadaye_hefdahe_shahrivar", R.string.shohadaye_hefdahe_shahrivar),
                        LocaleController.getString("haftome_tir", R.string.haftome_tir),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad)),
                mutableListOf(
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )

        //TajrishSub_FarhangsaraSub
        key = createKey(LocaleController.getString("tajrish_sub", R.string.tajrish_sub),
                LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shahed", R.string.shahed),
                        LocaleController.getString("emam_khomeini", R.string.emam_khomeini),
                        LocaleController.getString("sadegheye", R.string.sadegheye)),
                mutableListOf(
                        LocaleController.getString("shahed", R.string.shahed),
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("navab_safavi", R.string.navab_safavi),
                        LocaleController.getString("sadegheye", R.string.sadegheye))
        )

        //TajrishSub_AzadeganSub
        key = createKey(LocaleController.getString("tajrish_sub", R.string.tajrish_sub),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shahed", R.string.shahed),
                        LocaleController.getString("beheshti", R.string.beheshti),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )

        //TajrishSub_KolahdoozSub
        key = createKey(LocaleController.getString("tajrish_sub", R.string.tajrish_sub),
                LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("shahed", R.string.shahed),
                        LocaleController.getString("mohammadeyeh", R.string.mohammadeyeh),
                        LocaleController.getString("towhid", R.string.towhid),
                        LocaleController.getString("bimeh", R.string.bimeh)),
                mutableListOf(
                        LocaleController.getString("shahed", R.string.shahed),
                        LocaleController.getString("dowlat", R.string.dowlat),
                        LocaleController.getString("bimeh", R.string.bimeh))
        )

        //FarhangsaraSub_KolahdoozSub
        key = createKey(LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub),
                LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("erame_sabz", R.string.erame_sabz),
                        LocaleController.getString("bimeh", R.string.bimeh))
        )
        //FarhangsaraSub_AzadeganSub
        key = createKey(LocaleController.getString("farhangsara_sub", R.string.farhangsara_sub),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("sadegheye", R.string.sadegheye),
                        LocaleController.getString("shademan", R.string.shademan),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )
        //kolahdoozSub_AzadeganSub
        key = createKey(LocaleController.getString("kolahdooz_sub", R.string.kolahdooz_sub),
                LocaleController.getString("azadegan_sub", R.string.azadegan_sub))
        paths[key] = arrayListOf(
                mutableListOf(
                        LocaleController.getString("bimeh", R.string.bimeh),
                        LocaleController.getString("teatre_shahr", R.string.teatre_shahr),
                        LocaleController.getString("nobonyad", R.string.nobonyad))
        )
    }

}