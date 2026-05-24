package com.example.onemarket.presentation.profile

sealed class FaqListItem {
    data class SectionHeader(val title: String) : FaqListItem()
    data class Description(val text: String) : FaqListItem()
    data class Question(
        val question: String,
        val answer: String,
        var isExpanded: Boolean = false
    ) : FaqListItem()
}

object FaqData {

    fun buildList(): List<FaqListItem> = buildList {


        add(FaqListItem.SectionHeader("Sifarişin yerləşdirilməsi"))
        add(FaqListItem.Question(
            "1. OneMarket nədir?",
            "OneMarket - onlayn marketpleys, yəni satış platformasıdır. Burada müxtəlif satıcılardan olan malları eldə edə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "2. OneMarket mobil tətbiqi məndə yoxdursa, OneMarket-dən alış-veriş edə bilərəmmi?",
            "Bəli, onemarket.az saytında OneMarket mallarını eldə edə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "3. OneMarket mallarını oflayn mağazada eldə etmək mümkündürmü?",
            "Xeyr, Market OneMarket mallarını yalnız OneMarket mobil tətbiqində və ya onemarket.az saytında eldə edə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "4. Şəxsi və maliyyə məlumatlarımın təhlükəsizliyi necə təmin olunur?",
            "OneMarket Rabitə Nazirliyinin şəxsi məlumatların daxil edən sistemlərin reyestrinə daxil edilib. Şəxsi məlumatlarının saxlanılması və işlənilməsi prosesi Azerbaycan Respublikasının \"Fərdi məlumatlar haqqında\" qanunun tələblərinə uyğun göstərilir. Buna əlavə olaraq, OneMarket-də alış-veriş üçün istifadə etdiyiniz maliyyə məlumatlarınızı (misal üçün, kredit kartlarının məlumatlarını) saxlamırıq."
        ))
        add(FaqListItem.Question(
            "5. Telefon vasitəsi ilə sifariş yerləşdirmək mümkündürmü?",
            "Bəli, Çağrı mərkəzinə (915) müraciət edərək sifarişi yerləşdirə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "6. Alış-veriş edərkən, fiskal qəbz almaq mümkündürmü?",
            "Alış-veriş zamanı, qəbz həmişə təqdim edilir. Satıcı yeni nəsil kassalardan istifadə edirsə, qəbzdə ƏDV-nin geri alınması üçün QR-kod mövcud olacaq."
        ))
        add(FaqListItem.Question(
            "7. Niyə sifarişin yerləşdirilmə zamanı səbətdə olan sifarişlər bir necə fərqli sifarişə bölünür?",
            "Səbətinizdə müxtəlif satıcılardan olan məhsullar varsa, onlar fərqli anbarlardan fərqli sifarişlər kimi çatdırılacaqlar. Həmçinin, iri ölçülü məişət texnikası xüsusi çatdırılma şəraitlərini tələb etdiyinə görə, fərqli sifariş kimi çatdırılır."
        ))

        // ── Sifarişin Ödənilməsi ───────────────────────────────────────────────
        add(FaqListItem.SectionHeader("Sifarişin Ödənilməsi"))
        add(FaqListItem.Question(
            "1. Sifarişin ödənişini necə həyata keçirtmək olar?",
            "Sifarişin ödənişini sifarişi təhvil alarkən ödəniş kartı vasitəsilə terminalla həyata keçirə bilərsiniz. Terminalla ödəniş üçün istənilən bankın debet və kredit kartları qəbul olunur."
        ))
        add(FaqListItem.Question(
            "2. Məhsulları taksit ilə əldə etmək imkanı mövcuddurmu?",
            "Sifarişi Kapital Bank-ın Birbank taksit kartlarının istənilən biri ilə 24 aylıq hissəli ödəniş şərtləri üzrə ödəyə bilərsiniz."
        ))

        // ── Zəmanətlər ────────────────────────────────────────────────────────
        add(FaqListItem.SectionHeader("Zəmanətlər"))
        add(FaqListItem.Question(
            "1. OneMarket-dən əldə olunan məhsullara zəmanət verilirmi?",
            "Bəli, zəmanət onun mövcudluğu nəzərdə tutulan məhsullar üçün təqdim olunur. Zəmanət talonu məhsulla birlikdə təqdim olunur."
        ))
        add(FaqListItem.Question(
            "2. Zəmanət talonunu kim doldurur?",
            "Zəmanət talonu satıcı tərəfindən məhsulla birlikdə təqdim olunur. Çox vaxt o, qablaşdırmanın içində və ya sənədlər olan paketin içində yerləşir, ya da qablaşdırmanın üzərində olur. Quraşdırılma tələb edən mallar üçün talon doldurulmamış şəkildə təqdim olunur. Quraşdırılma tələb olunmayanda talon əvvəlcədən doldurulur."
        ))
        add(FaqListItem.Question(
            "3. Alıcı zəmanət talonu ilə hara müraciət edə bilər?",
            "Zəmanətdən istifadə etmək lazım olsa, siz məhsul istehsalçısının servis mərkəzinə müraciət edə biləcəksiniz. Servis mərkəzlərinin əlaqə məlumatları zəmanət talonunda göstərilib."
        ))
        add(FaqListItem.Question(
            "4. OneMarket satıcısından olan məhsulların zəmanətli təmiri necə həyata keçirilir?",
            "Zəmanət zəmanət müddətində istehsalçıdan asılı olan səbəblərə görə baş verən nasazlıqları pulsuz şəkildə təmir edilməsini nəzərdə tutur. Məhsulun zəmanət müddəti zəmanətli təmirin müddətinə uzadılır. Bu müddət istehlakçının OneMarket-in Xidmət mərkəzinə müraciət edilməsindən başlayaraq hesablanır. Xidmət mərkəzində baxış və ya təmir məqsədi ilə olan malların saxlanılması müddəti istehlakçının müraciət gününü nəzərə almadan 14 gün təşkil edə bilər. İstisnalı hallarda bu müddət 2 aya kimi uzadıla bilər. Təmir zamanı məhsulda qüsurlu detalların əvəzlənməsi məhsula olan zəmanət müddətinin tam şəkildə bərpa olunmasına səbəb olmur.\n\nZəmanət növbəti hallara aid olmur:\n• Məhsulun yaddaşında (o, mövcuddursa) məlumatların silinməsi və ya itməsi;\n• İstehlakçının məsləhət görülməyən aksessuarların istifadəsi nəticəsində yaranan nasazlıqlar;\n• İstehlakçının məhsulu nəzərdə tutulmayan məqsədlərdə istifadə etməsi nəticəsində yaranan nasazlıqlar;\n• İstehlakçı tərəfindən məhsulların istifadə etmə, saxlama və daşınma qaydaların pozulması;\n• Texniki və ya təhlükəsizlik tələblərinin pozulması nəticəsində yaranan nasazlıqlar."
        ))
        add(FaqListItem.Question(
            "5. Hansı hallarda zəmanət qüvvədən düşür?",
            "• Məhsulun mexaniki zədələnməsi təqdirdə;\n• Məhsul içinə maye və ya yad cisimlərin daxil olunması nəticəsində zədələndiyi təqdirdə;\n• Elektrik enerjisinin gərginliyinin dəyişilməsi nəticəsində zədələnməsi təqdirdə;\n• Məhsulun istifadə qaydalarının pozulması halında, o cümlədən, yüksək nəmişliyə və ya tozluluğa görə nasazlıqlar yarandığı təqdirdə;\n• Məhsulun açılması və ya təmiri üçüncü şəxs tərəfindən həyata keçirildiyi təqdirdə."
        ))
        add(FaqListItem.Question(
            "6. Məhsul zəmanətinin müddəti nə qədərdir?",
            "Məhsul zəmanəti haqqında məlumatı satıcı ilə dəqiqləşdirmək olar, müddət haqqında məlumat verilməyibsə, Azərbaycan Respublikasının Mülki Məcəlləsinin 589.2 maddəsinə əsaslanaraq 2 il ərzində Satıcıya zəmanət xidmətləri üçün müraciət edə bilərsiniz. Satıcıdan zəmanət xidmətləri sorğusuna imtina almısınızsa, 915 qaynar xətti və ya \"Mənim sifarişim\" bölməsindəki \"Müştəri vəkilinə müraciət\" düyməsi vasitəsilə bizimlə əlaqə saxlamağınızı xahiş edirik."
        ))

        // ── Məhsulun geri qaytarılması ────────────────────────────────────────
        add(FaqListItem.SectionHeader("Məhsulun geri qaytarılması"))
        add(FaqListItem.Question(
            "1. OneMarket-də alınmış məhsulu necə qaytarmaq olar?",
            "Məhsulu qaytarmaq üçün OneMarket tətbiqinə daxil olun, sonra \"Mənim sifarişlərim\" bölməsinə keçin və qaytarmaq istədiyiniz məhsulu seçin."
        ))
        add(FaqListItem.Question(
            "2. Məhsulun geri qaytarılması üçün hansı müddət ərzində müraciət etmək olar?",
            "Məhsulları çatdırılma tarixindən etibarən 14 gün ərzində əmtəə görünüşü pozulmadığı halda geri qaytara bilərsiniz. Əgər məhsul qüsurludursa, zəmanət müddəti ərzində müraciət edə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "3. Məhsulun çatdırılmasından 14 gündən çox vaxt keçibsə, onu geri qaytarmaq mümkündürmü?",
            "Məhsul qüsurludursa, geri qaytarılmanı zəmanət müddətində rəsmiləşdirmək mümkündür."
        ))
        add(FaqListItem.Question(
            "4. Geri qaytarılma sorğusu təsdiqləndikdən sonra məhsulu hara təqdim edə bilərəm?",
            "Müştərinin şəxsi kabinetində geri qaytarılma təsdiqləndikdən sonra, məhsulun geri qaytarılması müştəri tərəfindən seçilmiş sifarişin təhvil məntəqələrindən birinə müstəqil şəkildə həyata keçirilir. OneMarket məhsullarını geri qaytarmaq istəyirsinizsə, bunu qəbul edən təhvil məntəqələrinin ünvan və iş saatlarını «OneMarket təhvil məntəqələrinin siyahısı» bölməsində tapa bilərsiniz. «Qaytarılma» xanasını işarələdikdə, məhsul qaytarılan məntəqələrin aktual siyahısı görünəcək. İri həcmli malların geri qaytarılması isə müştəri ilə razılaşdırılmış şəkildə kuryer xidməti vasitəsilə həyata keçirilir."
        ))
        add(FaqListItem.Question(
            "5. OneMarket-də məhsulun geri qaytarılması prosesi nə qədər davam edir?",
            "Məhsulun geri qaytarılma müddəti şərtlərdən asılı olaraq dəyişə bilər. Geri qaytarılma müraciətini yaratdıqdan sonra, sifarişin detalları bölməsində geri qaytarılmanın statusunu izləyə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "6. Məhsulu geri qaytarmışam, vəsait nə vaxt geri qaytarılacaq?",
            "Məhsulu qaytardıqdan sonra vəsaitin geri qaytarılma müddəti ödəniş üsulundan asılı olaraq fərqlənə bilər.\n\nKartla ödəniş zamanı qaytarılma təsdiqləndikdən sonra vəsait hesabınıza 14 iş günü ərzində qaytarılacaq.\n\nKreditlə ödəniş zamanı qaytarılma təsdiqləndikdən sonra kredit 7 gün ərzində ləğv ediləcək. Əgər aylıq ödəniş edilibsə, bu məbləğ də 14 gün ərzində hesabınıza geri qaytarılacaq.\n\nTaksitlə ödəniş zamanı qaytarılma təsdiqləndikdən sonra vəsait 14 iş günü ərzində hesabınıza köçürüləcək."
        ))
        add(FaqListItem.Question(
            "7. Məhsul yanlış çatdırılıb. Nə etməliyəm?",
            "Əgər sizə yanlış məhsul çatdırılıbsa, OneMarket tətbiqinə daxil olun, sonra \"Sifarişlərim\" bölməsinə keçin və müvafiq məhsulu seçərək geri qaytarılma üçün müraciət edin."
        ))
        add(FaqListItem.Question(
            "8. Məhsulu qaytardığım təqdirdə qazandığım bonuslar silinəcəkmi?",
            "Məhsul qaytarıldıqda, əldə etdiyiniz bonuslar bonus hesabınızdan silinəcək."
        ))
        add(FaqListItem.Question(
            "9. Mən geri qaytarılma üçün müraciət etmişəm, onu ləğv edə bilərəm?",
            "Bəli. Əgər məhsulu seçdiyiniz təhvil məntəqəsinə və ya kuryerə təhvil verməmisinizsə, müraciəti ləğv edə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "10. Sifarişimdə bir neçə məhsul var, yalnız birini qaytara bilərəm?",
            "Bəli, sifarişinizdən yalnız bir məhsulu qaytara bilərsiniz."
        ))
        add(FaqListItem.Question(
            "11. Məhsul kreditlə alınıb, krediti ləğv edilməsi və vəsaitlərin geri qaytarılması nəçə günə baş tutacaq?",
            "Əgər sifariş kreditlə rəsmiləşdirilibsə, qaytarma müraciəti təsdiqləndikdən sonra kredit 7 gün ərzində ləğv ediləcək. Əgər kredit üzrə aylıq ödəniş etmisinizsə, həmin məbləğ 14 gün ərzində hesabınıza geri qaytarılacaq."
        ))
        add(FaqListItem.Question(
            "12. Məhsul işlək deyilsə, nə etməliyəm?",
            "Əgər aldığınız məhsul qüsurludursa, OneMarket tətbiqinə daxil olun, sonra \"Sifarişlərim\" bölməsinə keçin və müvafiq məhsulu seçərək geri qaytarma üçün müraciət edin."
        ))
        add(FaqListItem.Question(
            "13. Məhsulu qaytarmaq üçün hansı sənədlər tələb olunur?",
            "Məhsulu qaytarmaq üçün ödəniş qəbzi və zəmanət talonu (əgər təqdim olunubsa) təqdim etməlisiniz."
        ))
        add(FaqListItem.Question(
            "14. Azərpoçt vasitəsilə çatdırılan məhsulları necə qaytarmaq olar?",
            "Əgər sifarişiniz Azərpoçt vasitəsilə çatdırılıbsa və siz geri qaytarma etmək istəyirsinizsə, OneMarket tətbiqinə daxil olun, sonra \"Sifarişlərim\" bölməsinə keçin və müvafiq məhsulu seçərək qaytarma üçün müraciət edin. Əgər müraciətiniz təsdiqlənərsə, məhsulu Azərpoçt şöbəsinə gətirin və operatora geri qaytarılma barədə məlumat verin."
        ))
        add(FaqListItem.Question(
            "15. Məhsulu bölgədəki təhvil məntəqəsi vasitəsilə necə qaytarmaq olar?",
            "Əgər sifarişinizi bölgədəki OneMarket təhvil məntəqəsindən təhvil almısınızsa və məhsulu geri qaytarmaq istəyirsinizsə, OneMarket tətbiqinə daxil olun, sonra \"Sifarişlərim\" bölməsinə keçin və müvafiq məhsulu seçərək qaytarılma üçün müraciət edin."
        ))
        add(FaqListItem.Question(
            "16. Hansı məhsullar geri qaytarılmır?",
            "\"Azərbaycan Respublikası ərazisində pərakəndə ticarət obyektlərində dəyişdirilməli olmayan malların siyahısı haqqında\" qanuna əsasən, bəzi məhsullar geri qaytarıla bilməz. Bu məhsullar yalnız keyfiyyətsiz və ya qüsurlu olduqda geri qaytarıla bilər."
        ))

        // ── Taksitli ödəniş ───────────────────────────────────────────────────
        add(FaqListItem.SectionHeader("Taksitli ödəniş"))
        add(FaqListItem.Question(
            "1. OneMarket-də hansı kartlarla hissə-hissə (taksitlə) ödəniş etmək olar?",
            "İstənilən Birbank taksit kartı ilə hissəli ödəniş keçərlidir."
        ))
        add(FaqListItem.Question(
            "2. Hansı sifarişləri hissə-hissə (taksitlə) ödəmək olar?",
            "Hissəli ödəniş istənilən məbləğdə bütün sifarişlər üçün keçərlidir."
        ))
        add(FaqListItem.Question(
            "3. Hissə-hissə (taksitli) ödəniş hansı müddətə rəsmiləşdirilir?",
            "Hissəli ödəniş 3 aydan 24 aya qədər mümkündür."
        ))
        add(FaqListItem.Question(
            "4. Məhsulu hissə-hissə (taksitlə) ödəmək üçün nə etmək lazımdır?",
            "Hissəli ödənişi rəsmiləşdirmək üçün, ödənişi \"Birbank taksitli kartı ilə\" və ya \"məhsulu alarkən\" seçib, operatora və ya kuryerə xəbər edin."
        ))
        add(FaqListItem.Question(
            "5. Hissə-hissə (taksitli) ödənişə görə faiz və ya komissiya tutulur?",
            "Xeyr, hissəvi (taksitli) ödənişlərə görə faiz və ya komissiya tutulmur."
        ))
        add(FaqListItem.Question(
            "6. Məhsulu geri qaytarmaq istəsəm, hissə-hissə (taksitli) ödənişlə nə olacaq?",
            "Məhsulu geri qaytardığınız halda, ödəniş məbləği sizə qaytarılacaq və bu məbləği hissə-hissə (taksitli) ödənişi bağlamaq üçün istifadə edə biləcəksiniz."
        ))
        add(FaqListItem.Question(
            "7. Sifarişin (məhsulun) yalnız bir hissəsini hissə-hissə (taksitlə) ödəmək olar?",
            "Xeyr, sifarişin hissəvi (taksitli) ödənişi yalnız sifarişin bütöv məbləğinə olunur."
        ))

        // ── Bir ID — FAQ ──────────────────────────────────────────────────────
        add(FaqListItem.SectionHeader("Bir ID — FAQ"))
        add(FaqListItem.SectionHeader("Məhsulun təsviri"))
        add(FaqListItem.Description(
            "Bir ID — sizin bütün Bir ekosistemin üzrə vahid istifadəçi hesabınızdır. Əvvəllər hər tətbiqin öz login, öz məlumatları və öz şifrəsi var idi. İndi hər şey birləşdirilib: bircə girişlə siz Birbank, OneMarket, Bravo, m10 və digər servislərə daxil olursunuz. Məlumatlarınız avtomatik doldurulur, bonuslarınız hər yerdə əks olunur. Heç bir məlumatı yenidən daxil etməyə ehtiyac qalmır. Bu, myGov, Apple ID və ya Google hesabı ilə giriş kimidir — sadəcə Bir ekosistemi üçün nəzərdə tutulub, yoxlanılmış məlumatlara və bank səviyyəsində təhlükəsizliyə əsaslanır."
        ))
        add(FaqListItem.SectionHeader("İstifadəçilər üçün FAQ"))
        add(FaqListItem.Question(
            "1. Bir ID nədir?",
            "Bu, sizin Bir ekosisteminin bütün məhsulları ilə işləyən vahid istifadəçi hesabınızdır. Bircə girişlə hər şey əlçatan olur: Birbank, m10, OneMarket, Bravo və digər servislər. Artıq ayrıca istifadəçi hesabları yaratmağa və fərqli şifrələri yadda saxlamağa ehtiyac qalmır."
        ))
        add(FaqListItem.Question(
            "2. Bir ID nəyə lazımdır?",
            "Bir ID sizi artıq addımlardan azad edir. Bircə dəfə daxil olursunuz — məlumatlarınız və bonuslarınız artıq yerindədir. Telefon nömrənizi dəyişdiniz? Dərhal hər yerdə yenilənir."
        ))
        add(FaqListItem.Question(
            "3. Bir ID yaratmaq üçün Birbank tətbiqi lazımdır?",
            "Xeyr. Qeydiyyat üçün telefon nömrənizi təsdiqləməyiniz kifayətdir — Birbank tətbiqini yükləmək məcburi deyil. Bir ID artıq Birbank, m10, OneMarket və Bravo tətbiqlərində işləyir."
        ))
        add(FaqListItem.Question(
            "4. \"Bir ID ilə daxil ol\" düyməsi nə üçündür?",
            "Hər tətbiqdə hər dəfə yenidən qeydiyyatdan keçmək əvəzinə, sadəcə \"Bir ID ilə daxil ol\" düyməsini sıxırsınız və mövcud istifadəçi hesabınızla giriş edirsiniz. Sürətli şəkildə, əlavə məlumatları və şifrələri yazmadan."
        ))
        add(FaqListItem.Question(
            "5. Niyə Birbank tətbiqinə yönləndirilirəm?",
            "Bir ID-niz Birbank tətbiqində saxlanılır — yoxlanışdan keçmiş hesabınız oradadır. Başqa servisə Bir ID ilə daxil olarkən tətbiq qısa bir anlıq Birbank-ı açır ki, bunun həqiqətən siz olduğunuzu təsdiqləsin, sonra isə avtomatik olaraq sizi geri qaytarır. Bu, standart təhlükəsizlik prosedurudur — əlavə heç nə etməyə ehtiyac yoxdur."
        ))
        add(FaqListItem.Question(
            "6. Kartlarımı görəcəksiniz?",
            "Xeyr. Bir ID vasitəsilə daxil olduğunuz tətbiqlər yalnız profilinizin əsas məlumatlarını əldə edir — ad, telefon nömrəsi, ünvan — və yalnız sizin razılıq verdiyiniz məlumatları. Kart və bank məlumatlarınız ötürülmür."
        ))
        add(FaqListItem.Question(
            "7. Telefon nömrəm dəyişib. Bir ID hesabım necə olacaq?",
            "Yeni telefon nömrənizlə Birbank tətbiqinə daxil olun və verifikasiyadan keçin. Bundan sonra yeni nömrə avtomatik olaraq Bir ID-nizə bağlanacaq. Bütün məlumatlarınız və tarixçəniz saxlanılır, amma köhnə nömrəyə bağlı bütün tətbiqlərdən avtomatik çıxış ediləcək — yenidən daxil olmaq lazım olacaq."
        ))

        // ── Kredit ───────────────────────────────────────────────────────────
        add(FaqListItem.SectionHeader("Kredit"))
        add(FaqListItem.Question(
            "1. Kreditin maksimal məbləği nə qədərdir?",
            "Kreditin maksimal məbləği 15 000 AZN təşkil edir."
        ))
        add(FaqListItem.Question(
            "2. Kredit hansı müddətə verilir?",
            "2 aydan 24 aya kimi."
        ))
        add(FaqListItem.Question(
            "3. Krediti kim ala bilər?",
            "18 yaşından yuxarı olan Azərbaycan Respublikasının vətəndaşları."
        ))
        add(FaqListItem.Question(
            "4. İş stajına olan tələblər hansılardır?",
            "Son iş yerində minimal staj - 3 ay. Təqaüdçülər üçün iş yeri tələbləri yoxdur."
        ))
        add(FaqListItem.Question(
            "5. Faiz dərəcəsi və kredit ödənişlərinin məbləği nə qədərdir?",
            "Faiz dərəcəsi kreditin məbləğindən və müddətindən asılıdır. Ödənişlərinin təxmini məbləğini kredit müraciətini yerləşdirdikdə seçmək olar."
        ))
        add(FaqListItem.Question(
            "6. Kredit hansı bankda rəsmiləşdirilir?",
            "OneMarket-in kredit üzrə tərəfdaşı Kapital Bank-dır."
        ))
        add(FaqListItem.Question(
            "7. Məhsulları kreditlə necə almaq olar?",
            "Sifarişin rəsmiləşdirmə mərhələsində \"Sifarişi kreditlə rəsmiləşdirmək\" seçmək kifayətdir."
        ))
        add(FaqListItem.Question(
            "8. Krediti almaq üçün bank filialına müraciət etmək lazımdırmı?",
            "Xeyr, kreditin verilmə prosesi bütövlükdə onlayn şəkildə həyata keçirilir. Sizə internetə giriş və video zəngə qoşulmaq üçün kameralı telefon və ya noutbuk (kompüter) lazım olacaq."
        ))
        add(FaqListItem.Question(
            "9. Kreditin verilmə qərarı nə qədər tez qəbul olunur?",
            "Müştərinin tanınması uğurlu olarsa, kreditin rəsmiləşdirilməsi avtomatik şəkildə həyata keçirilə və kredit dərhal təsdiqlənə bilər."
        ))
        add(FaqListItem.Question(
            "10. Mən bir neçə məhsula kredit rəsmiləşdirmişəm, lakin onlardan birini (bir nəçəsini) almaq fikrimi dəyişdim. Kreditimlə nə olacaq?",
            "Kreditin məbləği almaq istədiyiniz məhsullara görə yenidən hesablanacaq."
        ))
        add(FaqListItem.Question(
            "11. Müraciət etdiyim məhsulu əvəzləmək olarmı?",
            "Xeyr. Digər məhsulu kreditlə almaq üçün yeni sifarişi rəsmiləşdirmək lazımdır."
        ))
        add(FaqListItem.Question(
            "12. Mənim kredit müraciətim qəbul olunmadı, sifarişimlə nə olacaq?",
            "Sifarişi əldə etmək üçün siz digər ödəniş üsulunu, kart vasitəsilə olan ödənişi seçə bilərsiniz."
        ))
        add(FaqListItem.Question(
            "13. Mən krediti rəsmiləşdirmişəm, lakin məhsulu almaq fikrimi dəyişdim. Kreditimlə nə olacaq?",
            "Kredit müraciətiniz 10 gün ərzində avtomatik şəkildə ləğv olunacaq."
        ))
        add(FaqListItem.Question(
            "14. Krediti vaxtından əvvəl bağlaya bilərəmmi?",
            "Bəli, komissiyasız və cəriməsiz vaxtından əvvəl bağlama nəzərdə tutulub."
        ))
        add(FaqListItem.Question(
            "15. Məhsulun ödənişinin yarısı varımdır, qalan hissəyə kredit rəsmiləşdirə bilərəmmi?",
            "Bəli, əgər kredit limiti kifayət etmirsə, müştəri bir hissəni öz vəsaiti ilə ödəyərək qalan məbləğ üçün kredit rəsmiləşdirə bilər."
        ))
        add(FaqListItem.Question(
            "16. Kreditim haqqında olan məlumata (faiz dərəcəsinə, borcun qalıq hissəsinə) harada baxa bilərəm?",
            "Kreditə aid bütün məlumatları internet bankinqdə və Kapital Bank-dan olan Birbank mobil tətbiqində baxa bilərsiniz. Bu məlumat kreditlə aldığınız məhsulu əldə etdikdən sonra mövcud olacaq."
        ))
        add(FaqListItem.Question(
            "17. Kredit müqaviləsi haqqında şəhadətnaməni alarkən sənədlər üstümdə olmalıdırmı? Hansı sənədlər olmalıdır?",
            "Kuryer sənədi çatdırarkən şəxsiyyət vəsiqənizin üstünüzdə olması vacibdir!"
        ))
        add(FaqListItem.Question(
            "18. Borc haqqında məlumatı necə əldə etmək olar?",
            "• Birbank tətbiqində Şəxsi kabinetə daxil olaraq;\n• 196 nömrəsinə zəng vuraraq Dəstək Mərkəzi ilə əlaqə saxlamaqla və ya Onlayn Çatda mesaj yazmaqla;\n• Ödəniş terminalları vasitəsilə."
        ))
        add(FaqListItem.Question(
            "19. Borc ödənilərkən hansı məlumatlar daxil edilməlidir?",
            "• Müştəri kodu (CIF) və doğum tarixi;\n• Ödənişi Birbank tətbiqində şəxsi kabinetə daxil olaraq etmək olar."
        ))
        add(FaqListItem.Question(
            "20. Borcu ödəyərkən nəyi bilmək lazımdır?",
            "Kredit ödənişi ayın 1-dən 20-dək (daxil olmaqla) saat 17:00-a qədər həyata keçirilməlidir.\n\nƏgər tələb olunan ödəniş 20 gün ərzində saat 17:00-dək edilməzsə, bank aşağıdakı tədbirləri görə bilər:\n\n• Ödəniş məbləğini şəxsi hesabdan bloklaya bilər;\n• Kredit limitini ləğv edə bilər;\n• Əgər telefon OneMarket vasitəsilə kreditlə alınıbsa, mobil şəbəkəni bloklaya bilər.\n\nAyın 20-si saat 17:00-dan sonra həyata keçirilən ödəniş növbəti gün təsdiqlənir, yəni 1 günlük gecikmə kimi qeydə alınır.\n\nTələb olunan məbləğ ödənildikdən sonra bloklanmış vəsaitlər bərpa olunur."
        ))
        add(FaqListItem.Question(
            "21. Borcu necə ödəmək olar?",
            "• Birbank tətbiqində Şəxsi kabinetə daxil olaraq;\n• Kapital Bank, Emanat və MilliÖn terminalları vasitəsilə CIF kodu və doğum tarixi göstərilməklə.\n\nQeyd: Ödəniş CIF kodu və doğum tarixi göstərilməklə Birbank tətbiqi vasitəsilə də həyata keçirilə bilər."
        ))
    }
}
