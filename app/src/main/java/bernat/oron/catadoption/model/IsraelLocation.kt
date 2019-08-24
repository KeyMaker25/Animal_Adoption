package bernat.oron.catadoption.model



class IsraelDistricts {

    val North = arrayListOf("נצרת",
        "נהריה",
        "עכו",
        "כרמיאל",
        "טבריה",
        "עפולה",
        "נוף הגליל",
        "שפרעם",
        "טמרה",
        "צפת",
        "סח'נין",
        "מגדל העמק",
        "קריית שמונה",
        "מעלות-תרשיחא",
        "יקנעם עילית",
        "בית שאן")
    val Haifa = arrayListOf(
        "חיפה",
        "חדרה",
        "קריית אתא",
        "אום אל-פחם",
        "קריית מוצקין",
        "קריית ים",
        "קריית ביאליק",
        "באקה-ג'ת",
        "נשר",
        "טירת כרמל",
        "אור עקיבא"
    )
    val Jeruzalem = arrayListOf(
        "ירושלים",
        "בית שמש"
    )
    val Center =  arrayListOf(
        "ראשון לציון",
        "פתח תקווה",
        "נתניה",
        "רחובות",
        "כפר סבא",
        "מודיעין-מכבים-רעות",
        "רמלה",
        "לוד",
        "רעננה",
        "הוד השרון",
        "ראש העין",
        "נס ציונה",
        "אלעד",
        "יבנה",
        "טייבה",
        "יהוד-מונוסון",
        "גבעת שמואל",
        "טירה",
        "כפר קאסם",
        "כפר יונה",
        "קלנסווה"
    )
    val TelAviv = arrayListOf(
        "תל אביב-יפו",
        "חולון",
        "בני ברק",
        "רמת גן",
        "בת ים",
        "הרצליה",
        "גבעתיים",
        "רמת השרון",
        "אור יהודה",
        "קריית אונו"
    )
    val South = arrayListOf(
        "אשדוד",
        "באר שבע",
        "אשקלון",
        "רהט",
        "קריית גת",
        "אילת",
        "דימונה",
        "נתיבות",
        "אופקים",
        "ערד",
        "שדרות",
        "קריית מלאכי"
    )

    fun getall(): ArrayList<String> {
        var list = ArrayList<String>()
        list.addAll(this.North)
        list.addAll(this.Haifa)
        list.addAll(this.Jeruzalem)
        list.addAll(this.Center)
        list.addAll(this.TelAviv)
        list.addAll(this.South)

        return list
    }
}