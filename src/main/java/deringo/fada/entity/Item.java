package deringo.fada.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuppressWarnings("unused")
public class Item {

    /*
    <item>
      <title>Dr. Catharina Hamm über Herzgesundheit</title>
      <link>https://storage01.sr.de/podcast/SR2_FADA_P/SR2_FADA_P_38982_20250310_103741.MP3</link>
      <itunes:subtitle xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">Kardiologin Dr. Catharina Hamm spricht darüber, was wir alle präventiv für ein gesundes Herz tun können, aber auch, inwiefern sich Herzprobleme bei Frauen möglicherweise ganz anders und auch später zeigen als bei Männern.</itunes:subtitle>
      <itunes:summary xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">Männer sind furchtbar stark, Männer können alles, Männer kriegen ´nen Herzinfarkt - die Älteren erinnern sich an Herbert Grönemeyers ironisches Lied über unser Männerbild - das war 1984 - längst überholt - könnte man denken - denn wir wissen natürlich: Auch Frauen können einen Herzinfarkt erleiden. Trotzdem sind es noch immer die Männer, deren Gesundheit häufig in den Fokus gerückt wird, wenn es um Herzerkrankungen geht. Unser heutiger Gast spricht von einer Vermännlichung der Herzmedizin und Vernachlässigung der Frauen- Herzgesundheit - und sie möchte Auswege zeigen, die nicht nur Frauen, sondern alle Besitzer eines Herzens interessieren werden."Save Your Heart - Starte deinen Weg in ein herzgesundes Leben” ist der Titel des Buches - Dr. Catharina Hamm ist Internistin und Kardiologin - sie spricht darüber, was wir alle präventiv für ein gesundes Herz tun können, welche Bedeutung die richtige Ernährung und Sport aber auch die Psyche haben, aber auch, inwiefern sich Herzprobleme bei Frauen möglicherweise ganz anders und auch später zeigen als bei Männern. Moderation: Kai Schmieding - Das Buch: https://www.dtv.de/buch/save-your-heart-44540 // Unser Podcast-Tipp: WDR 2 Frag dich fit - mit Doc Esser und Anne findet sich in der ARD Audiothek unter: https://www.ardaudiothek.de/sendung/wdr-2-frag-dich-fit-mit-doc-esser-und-anne/72578238/</itunes:summary>
      <itunes:image xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" href="https://sr-mediathek.de/pcast/img/SR2_FADA_P/38982_SR2_FADA_P_1741503658~_v-sr_Square_1400.jpg?1e088589cfbef2ee39de5c1cd882c5d0" />
      <itunes:author xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">Kai Schmieding</itunes:author>
      <itunes:keywords xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">Herzinfarkt;Frauentag;Cholesterin;Bluthochdruck;Adipositas;Ernährung;Vorhofflimmern;Diabetes;Hormone</itunes:keywords>
      <itunes:explicit xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">no</itunes:explicit>
      <description>Kardiologin Dr. Catharina Hamm spricht darüber, was wir alle präventiv für ein gesundes Herz tun können, aber auch, inwiefern sich Herzprobleme bei Frauen möglicherweise ganz anders und auch später zeigen als bei Männern.</description>
      <content:encoded>&lt;p&gt;Männer sind furchtbar stark, Männer können alles, Männer kriegen ´nen Herzinfarkt - die Älteren erinnern sich an Herbert Grönemeyers ironisches Lied über unser Männerbild - das war 1984 - längst überholt - könnte man denken - denn wir wissen natürlich: Auch Frauen können einen Herzinfarkt erleiden. Trotzdem sind es noch immer die Männer, deren Gesundheit häufig in den Fokus gerückt wird, wenn es um Herzerkrankungen geht. Unser heutiger Gast spricht von einer Vermännlichung der Herzmedizin und Vernachlässigung der Frauen- Herzgesundheit - und sie möchte Auswege zeigen, die nicht nur Frauen, sondern alle Besitzer eines Herzens interessieren werden.&lt;/p&gt;&lt;p&gt;“Save Your Heart - Starte deinen Weg in ein herzgesundes Leben” ist der Titel des Buches - Dr. Catharina Hamm ist Internistin und Kardiologin - sie spricht darüber, was wir alle präventiv für ein gesundes Herz tun können, welche Bedeutung die richtige Ernährung und Sport aber auch die Psyche haben, aber auch, inwiefern sich Herzprobleme bei Frauen möglicherweise ganz anders und auch später zeigen als bei Männern. Moderation: Kai Schmieding&lt;/p&gt;&lt;br&gt;&lt;p&gt;Das Buch: https://www.dtv.de/buch/save-your-heart-44540&lt;/p&gt;&lt;br&gt;&lt;p&gt;Unser Podcast-Tipp: &lt;strong&gt;WDR 2 Frag dich fit - mit Doc Esser und Anne&lt;/strong&gt; findet sich in der ARD Audiothek unter: https://www.ardaudiothek.de/sendung/wdr-2-frag-dich-fit-mit-doc-esser-und-anne/72578238/&lt;/p&gt;</content:encoded>
      <enclosure url="https://storage01.sr.de/podcast/SR2_FADA_P/SR2_FADA_P_38982_20250310_103741.MP3" length="54479276" type="audio/mpeg" />
      <pubDate>Sun, 09 Mar 2025 09:29:00 GMT</pubDate>
      <guid isPermaLink="false">SR2_FADA_P_38982_20250310_103741.MP3</guid>
      <dc:creator>fragen-an-den-autor@sr.de</dc:creator>
      <dc:date>2025-03-09T09:29:00Z</dc:date>
    </item>
    */
    
    @Id
    private String guid;
    
    private String title;
    private String description;
    private String content;
    //@Temporal(TemporalType.TIMESTAMP)
    private Date   pubDate;
    
    private String url;
    private String filename;
    private Long   length;
    private String type;
    
    private String subtitle;
    private String summary;
    private String image;
    private String author;
    private String keywords;

}