import axios from "axios";

// Datentyp für den Podcast
export interface Podcast {
  guid: string;
  title: string;
  description: string;
  content: string;
  pubDate: Date;

  url: string;
  filename: string;
  length: number;
  type: string;

  subtitle: string;
  summary: string;
  image: string;
  author: string;
  keywords: string;
}

// API-Funktion zum Laden der Artikel
export async function fetchPodcasts(): Promise<Podcast[]> {
  try {
    const response = await axios.get("/api/feed");
    return response.data.map((podcast: Podcast) => ({
          ...podcast,
          pubDate: new Date(podcast.pubDate), // Umwandlung hier
        }));
  } catch (error) {
    console.error("Fehler beim Laden des Feeds:", error);
    return []; // Falls ein Fehler auftritt, geben wir ein leeres Array zurück
  }
}
