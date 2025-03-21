import { useEffect, useState } from "react";
import { fetchPodcasts, Podcast } from "./api";

function App() {
  const [articles, setPodcasts] = useState<Podcast[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPodcasts().then(data => {
      setPodcasts(data);
      setLoading(false);
    });
  }, []);

  if (loading) {
    return <p>Lade Podcasts...</p>;
  }

  return (
    <div>
      <h1>Fragen an den Author</h1>
      <ul>
        {articles.map((podcast, index) => (
          <li key={index}>
            <strong>{podcast.title}</strong><br />
            <small>{podcast.pubDate.toLocaleDateString("de-DE")}&nbsp;<a href={`files/${podcast.filename}`}>Datei</a></small>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
