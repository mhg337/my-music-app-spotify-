'use client';

import { useState, useEffect } from 'react';

// 🌐 접속 환경에 따라 자동으로 백엔드 주소를 바꿔주는 마법의 변수
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

const getGenreColor = (genre: string) => {
  const predefined: Record<string, string> = {
    "K-Pop": "#1DB954", "Pop": "#1da1f2", "Hip-Hop/Rap": "#ff4b4b",
    "R&B/Soul": "#f4c20d", "Alternative": "#a333c8", "Dance": "#ff007f", "Rock": "#ff6600", "Jazz": "#00ffcc"
  };
  if (predefined[genre]) return predefined[genre];

  let hash = 0;
  for (let i = 0; i < genre.length; i++) hash = genre.charCodeAt(i) + ((hash << 5) - hash);
  return `hsl(${Math.abs(hash) % 360}, 70%, 55%)`;
};

export default function MusicDashboard() {
  const [currentTrack, setCurrentTrack] = useState("선택된 곡이 없습니다");

  type Playlist = {
    id: string;
    name: string;
    tracks: {
      title: string;
      uri: string;
      genre: string;
    }[];
  };

  const [chartList, setChartList] = useState<any[]>([]);
  const [isChartLoading, setIsChartLoading] = useState(false);

  useEffect(() => {
    const fetchChart = async () => {
      setIsChartLoading(true);
      try {
        const res = await fetch(`${API_BASE_URL}/api/chart`);
        const data = await res.json();
        setChartList(data);
      } catch (e) {
        console.error("차트 로딩 실패:", e);
      }
      setIsChartLoading(false);
    };
    fetchChart();
  }, []);

  const addChartTrackToPlaylist = async (track: any) => {
    if (!selectedPlaylistId) {
      alert("먼저 곡을 담을 플레이리스트를 선택해주세요!");
      return;
    }

    const searchQuery = `${track.title} ${track.artist}`;

    try {
      const res = await fetch(`${API_BASE_URL}/api/search?q=${encodeURIComponent(searchQuery)}`);
      const uri = await res.text();

      if (uri !== "NOT_FOUND") {
        const newTrack = {
          title: `${track.title} - ${track.artist}`,
          uri: uri,
          genre: "K-Pop"
        };
        addToPlaylist(newTrack);
        alert(`'${track.title}' 곡이 추가되었습니다!`);
      } else {
        alert("스포티파이에서 해당 곡의 재생 주소를 찾을 수 없습니다.");
      }
    } catch (e) {
      alert("서버 연결 실패: 곡을 추가할 수 없습니다.");
    }
  };

  const [playlists, setPlaylists] = useState<Playlist[]>([]);
  const [selectedPlaylistId, setSelectedPlaylistId] = useState("");
  const [recommendedList, setRecommendedList] = useState<{ title: string; uri: string; genre: string }[]>([]);
  const currentPlaylist = playlists.find(p => p.id === selectedPlaylistId);

  const [keyword, setKeyword] = useState("");
  const [playingList, setPlayingList] = useState<{ title: string; uri: string; genre: string }[]>([]);
  const [playingIndex, setPlayingIndex] = useState(-1);
  const [history, setHistory] = useState<{ title: string; genre: string }[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const PREDEFINED_GENRES = ["K-Pop", "Pop", "Hip-Hop/Rap", "R&B/Soul", "Dance", "Jazz"];

  const createPlaylist = async () => {
    const name = prompt("새 플레이리스트 이름");
    if (!name) return;

    try {
      const res = await fetch(`${API_BASE_URL}/api/playlists/create?name=${encodeURIComponent(name)}`);
      const newId = await res.text();

      if (newId !== "ERROR") {
        const newPlaylist = { id: newId, name: name, tracks: [] };
        setPlaylists([...playlists, newPlaylist]);
        setSelectedPlaylistId(newId);
      }
    } catch (e) {
      alert("서버 연결 실패: 플레이리스트를 생성할 수 없습니다.");
    }
  };

  useEffect(() => {
    const fetchPlaylists = async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/playlists`);
        const data = await res.json();
        setPlaylists(data);
      } catch (e) {
        console.error("서버에서 플레이리스트를 불러올 수 없습니다.", e);
      }
    };
    fetchPlaylists();

    const savedHistory = localStorage.getItem('jbeat_history');
    if (savedHistory) {
      setHistory(JSON.parse(savedHistory));
    }
  }, []);

  const renamePlaylist = async () => {
    if (!selectedPlaylistId) return;
    const newName = prompt("새 이름");
    if (!newName) return;

    try {
      await fetch(`${API_BASE_URL}/api/playlists/rename?id=${selectedPlaylistId}&newName=${encodeURIComponent(newName)}`);
      const updated = playlists.map(pl =>
        pl.id === selectedPlaylistId ? { ...pl, name: newName } : pl
      );
      setPlaylists(updated);
    } catch (e) {
      alert("서버 연결 실패: 이름을 변경할 수 없습니다.");
    }
  };

  const handleSearch = async () => {
    if (!keyword) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/search?q=${encodeURIComponent(keyword)}`);
      const uri = await res.text();

      if (uri === "NOT_FOUND") {
        alert("검색 결과가 없습니다.");
      } else {
        let realGenre = "Unknown";
        let formattedTitle = keyword;

        try {
          const itunesRes = await fetch(`https://itunes.apple.com/search?term=${encodeURIComponent(keyword)}&entity=song&limit=1&country=KR&lang=ko_kr`);
          const itunesData = await itunesRes.json();

          if (itunesData.results && itunesData.results.length > 0) {
            const item = itunesData.results[0];
            realGenre = item.primaryGenreName || "Unknown";
            const cleanTitle = item.trackName.replace(/\s*\(.*?\)\s*/g, '').replace(/\s*\[.*?\]\s*/g, '').trim();
            const cleanArtist = item.artistName.split('&')[0].split(',')[0].trim();
            formattedTitle = `${cleanTitle} - ${cleanArtist}`;
          }
        } catch (err) {
          console.error("아이튠즈 정보 파싱 에러:", err);
        }

        const newTrack = { title: formattedTitle, uri: uri, genre: realGenre };
        addToPlaylist(newTrack);
        setKeyword("");
      }
    } catch (e) {
      console.error("검색 실패:", e);
      alert("서버 연결 실패: 곡을 검색할 수 없습니다.");
    }
  };

  const handleGenreRecommend = async (genre: string) => {
    setIsLoading(true);
    setRecommendedList([]);
    try {
      const storeOption = genre === "K-Pop" ? "&country=KR&lang=ko_kr" : "&country=US";
      const res = await fetch(`https://itunes.apple.com/search?term=${encodeURIComponent(genre)}&entity=song&limit=100${storeOption}`);
      const data = await res.json();
      const results = [...data.results];

      for (let i = results.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [results[i], results[j]] = [results[j], results[i]];
      }

      if (data.results.length === 0) {
        alert("해당 장르의 곡을 찾을 수 없습니다.");
        setIsLoading(false);
        return;
      }

      const newTracks = [];
      const seenUris = new Set();
      const seenTitles = new Set();

      for (const item of results) {
        if (newTracks.length >= 10) break;

        const lowerTitle = item.trackName.toLowerCase();
        if (lowerTitle.includes('inst.') || lowerTitle.includes('instrumental')) continue;
        if (genre !== "K-Pop" && item.primaryGenreName?.toLowerCase().includes("k-pop")) continue;
        if (genre === "K-Pop" && !item.primaryGenreName?.toLowerCase().includes("k-pop")) continue;

        let cleanTitle = item.trackName.replace(/\s*\(.*?\)\s*/g, '').replace(/\s*\[.*?\]\s*/g, '').trim();
        let cleanArtist = item.artistName.split('&')[0].split(',')[0].trim();
        const uniqueKey = `${cleanTitle.toLowerCase()}_${cleanArtist.toLowerCase()}`;

        if (seenTitles.has(uniqueKey)) continue;
        seenTitles.add(uniqueKey);

        const searchQuery = `track:${cleanTitle} artist:${cleanArtist}`;
        try {
          const spotifyRes = await fetch(`${API_BASE_URL}/api/search?q=${encodeURIComponent(searchQuery)}`);
          const uri = await spotifyRes.text();
          const blockedTracks = ["k-pop - travis scott"];
          const uniqueName = `${cleanTitle} - ${cleanArtist}`.toLowerCase();

          if (blockedTracks.includes(uniqueName)) continue;

          if (uri !== "NOT_FOUND" && !seenUris.has(uri)) {
            seenUris.add(uri);
            newTracks.push({
              title: `${cleanTitle} - ${cleanArtist}`,
              uri: uri,
              genre: item.primaryGenreName || genre
            });
          }
        } catch (err) {
          console.error("스포티파이 검색 에러:", err);
        }
      }

      if (newTracks.length > 0) {
        setRecommendedList(newTracks);
      } else {
        alert("스포티파이 매칭에 실패했습니다. 다른 장르를 시도해주세요.");
      }
    } catch (e) {
      alert("추천 기능을 불러올 수 없습니다.");
    }
    setIsLoading(false);
  };

  const addToPlaylist = async (track: { title: string; uri: string; genre: string }) => {
    if (!selectedPlaylistId) {
      alert("먼저 곡을 담을 플레이리스트를 선택하세요.");
      return;
    }
    try {
      const url = `${API_BASE_URL}/api/playlists/add?id=${selectedPlaylistId}&title=${encodeURIComponent(track.title)}&uri=${encodeURIComponent(track.uri)}&genre=${encodeURIComponent(track.genre || "Unknown")}`;
      const res = await fetch(url);
      const status = await res.text();

      if (status === "OK") {
        const updated = playlists.map(pl => {
          if (pl.id !== selectedPlaylistId) return pl;
          return { ...pl, tracks: [...pl.tracks, track] };
        });
        setPlaylists(updated);
      } else {
        alert("서버 오류로 곡을 담지 못했습니다.");
      }
    } catch (e) {
      alert("서버 연결 실패: 곡을 담을 수 없습니다.");
    }
  };

  const deletePlaylist = async () => {
    if (!selectedPlaylistId) return;
    if (confirm("이 플레이리스트를 정말 삭제하시겠습니까?")) {
      try {
        await fetch(`${API_BASE_URL}/api/playlists/delete?id=${selectedPlaylistId}`);
        const updated = playlists.filter(p => p.id !== selectedPlaylistId);
        setPlaylists(updated);
        setSelectedPlaylistId("");
      } catch (e) {
        alert("서버 연결 실패: 플레이리스트를 삭제할 수 없습니다.");
      }
    }
  };

  const clearPlaylist = async () => {
    if (!selectedPlaylistId) {
      alert("선택된 플레이리스트가 없습니다.");
      return;
    }
    if (playingIndex === -1 || playingList.length === 0) {
      alert("현재 재생 중인 곡이 없습니다.");
      return;
    }

    const trackToRemove = playingList[playingIndex];
    if (confirm(`현재 재생 중인 '${trackToRemove.title}' 곡을 삭제하시겠습니까?`)) {
      try {
        await fetch(`${API_BASE_URL}/api/playlists/removeTrack?id=${selectedPlaylistId}&uri=${encodeURIComponent(trackToRemove.uri)}`);
        const updated = playlists.map(pl => {
          if (pl.id !== selectedPlaylistId) return pl;
          const newTracks = pl.tracks.filter(t => t.uri !== trackToRemove.uri);
          return { ...pl, tracks: newTracks };
        });
        setPlaylists(updated);
      } catch (e) {
        alert("서버 연결 실패: 곡을 삭제할 수 없습니다.");
      }
    }
  };

  const clearHistory = () => {
    setHistory([]);
    localStorage.removeItem('jbeat_history');
  };

  const playFromIndex = async (list: { title: string; uri: string; genre: string }[], startIndex: number, listType: 'MY' | 'REC') => {
    setPlayingList(list);
    setPlayingIndex(startIndex);

    const targetTrack = list[startIndex];
    setCurrentTrack(`🎵 재생 중: ${targetTrack.title} ${listType === 'REC' ? '(추천곡)' : ''}`);

    const safeGenre = targetTrack.genre || "Unknown";
    const updatedHistory = [...history, { title: targetTrack.title, genre: safeGenre }];
    setHistory(updatedHistory);
    localStorage.setItem('jbeat_history', JSON.stringify(updatedHistory));

    try {
      await fetch(`${API_BASE_URL}/api/play?uri=${targetTrack.uri}`);
      for (let i = startIndex + 1; i < list.length; i++) {
        await fetch(`${API_BASE_URL}/api/queue?uri=${list[i].uri}`);
        await new Promise(resolve => setTimeout(resolve, 600));
      }
    } catch (error) {}
  };

  const previousTrack = () => {
    if (playingList.length === 0) return;
    let targetIdx = playingIndex - 1;
    if (targetIdx < 0) targetIdx = 0;
    playFromIndex(playingList, targetIdx, 'MY');
  };

  const nextTrack = () => {
    if (playingList.length === 0) return;
    let targetIdx = playingIndex + 1;
    if (targetIdx >= playingList.length) {
      setCurrentTrack("🛑 마지막 곡입니다");
      return;
    }
    playFromIndex(playingList, targetIdx, 'MY');
  };

  const pauseTrack = async () => {
    setCurrentTrack("⏸️ 일시 정지됨");
    await fetch(`${API_BASE_URL}/api/pause`);
  };

  const resumeTrack = async () => {
    if (playingList.length > 0 && playingIndex >= 0) {
      setCurrentTrack(`▶️ 다시 재생 중: ${playingList[playingIndex].title}`);
    }
    await fetch(`${API_BASE_URL}/api/resume`);
  };

  const genreCounts = history.reduce((acc, track) => {
    const safeGenre = track.genre || "Unknown";
    acc[safeGenre] = (acc[safeGenre] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const totalListens = history.length;
  let currentAngle = 0;
  const pieGradients = Object.entries(genreCounts).map(([genre, count]) => {
    const percentage = (count / totalListens) * 100;
    const startAngle = currentAngle;
    const endAngle = currentAngle + percentage;
    currentAngle = endAngle;
    return `${getGenreColor(genre)} ${startAngle}% ${endAngle}%`;
  }).join(", ");

  const renderTrackTitle = (fullTitle: string) => {
    if (fullTitle.includes(" - ")) {
      const parts = fullTitle.split(" - ");
      const title = parts[0];
      const artist = parts.slice(1).join(" - ");
      return (
        <div style={{ marginBottom: '8px' }}>
          <strong style={{ color: 'white', fontSize: '1.05rem' }}>{title}</strong>
          <span style={{ color: '#aaa', fontSize: '0.9rem', marginLeft: '8px' }}>- {artist}</span>
        </div>
      );
    }
    return <div style={{ marginBottom: '8px', color: 'white', fontWeight: 'bold' }}>{fullTitle}</div>;
  };

  const controlButtonStyle = { padding: '12px 25px', fontSize: '16px', cursor: 'pointer', backgroundColor: '#333', color: 'white', border: '1px solid #555', borderRadius: '50px', fontWeight: 'bold' };
  const playButtonStyle = { padding: '12px 25px', fontSize: '16px', cursor: 'pointer', backgroundColor: '#1DB954', color: 'white', border: 'none', borderRadius: '50px', fontWeight: 'bold' };
  const miniBtnStyle = { padding: '5px 10px', fontSize: '12px', cursor: 'pointer', color: 'white', border: 'none', borderRadius: '5px', fontWeight: 'bold' };

  return (
    <div style={{ padding: '50px', fontFamily: 'sans-serif', backgroundColor: '#121212', color: 'white', minHeight: '100vh' }}>
      <h1 style={{ color: '#1DB954', textAlign: 'center', marginBottom: '30px' }}>🎧 J-Beat Music Dashboard</h1>

      <div style={{ backgroundColor: '#282828', padding: '20px', borderRadius: '10px', marginBottom: '20px', textAlign: 'center', fontSize: '1.2rem' }}>
        현재 상태: <strong style={{ color: '#1DB954' }}>{currentTrack}</strong>
      </div>
      <div style={{ display: 'flex', gap: '15px', justifyContent: 'center', marginBottom: '30px' }}>
        <button onClick={previousTrack} style={controlButtonStyle}>⏪ 이전</button>
        <button onClick={pauseTrack} style={controlButtonStyle}>⏸️ 정지</button>
        <button onClick={resumeTrack} style={{ ...controlButtonStyle, borderColor: '#1DB954' }}>▶️ 재생</button>
        <button onClick={nextTrack} style={controlButtonStyle}>⏭️ 다음</button>
      </div>

      <div style={{ backgroundColor: '#181818', padding: '20px', borderRadius: '10px', marginBottom: '30px', textAlign: 'center' }}>
        <h3 style={{ marginBottom: '15px', color: '#ccc' }}>✨ 장르 버튼을 눌러 추천 곡을 찾아보세요</h3>
        <div style={{ display: 'flex', gap: '10px', justifyContent: 'center', flexWrap: 'wrap', marginBottom: '20px' }}>
          {PREDEFINED_GENRES.map(genre => (
            <button
              key={genre} onClick={() => handleGenreRecommend(genre)} disabled={isLoading}
              style={{ padding: '10px 20px', backgroundColor: getGenreColor(genre), color: 'white', border: 'none', borderRadius: '30px', cursor: isLoading ? 'wait' : 'pointer', fontWeight: 'bold' }}
            >
              {genre} 추천받기
            </button>
          ))}
        </div>
        {isLoading && <p style={{ color: '#1DB954', marginBottom: '20px' }}>곡을 완벽하게 매칭 중입니다... 잠시만 기다려주세요 ⏳</p>}

        <div>
          <input
            value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="직접 곡 검색 후 내 리스트에 담기"
            style={{ padding: '12px', width: '300px', borderRadius: '5px', border: 'none', marginRight: '10px' }}
          />
          <button onClick={handleSearch} style={playButtonStyle}>🔍 검색 후 담기</button>
        </div>
      </div>

      <hr style={{ borderColor: '#333', marginBottom: '30px' }} />

      <div style={{
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '30px',
        width: '100%',
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '20px'
      }}>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <div style={{ flex: 1, backgroundColor: '#202020', padding: '20px', borderRadius: '10px', border: '1px solid #444' }}>
            <h2 style={{ borderBottom: '1px solid #333', paddingBottom: '10px', color: '#1da1f2' }}>✨ 추천 결과 ({recommendedList.length}곡)</h2>
            {recommendedList.length === 0 ? (
               <p style={{ color: '#888', marginTop: '20px', textAlign: 'center' }}>위의 장르 버튼을 눌러보세요!</p>
            ) : (
              <ol style={{ paddingLeft: '20px', minHeight: '200px' }}>
                {recommendedList.map((track, idx) => (
                  <li key={idx} style={{ margin: '15px 0' }}>
                    {renderTrackTitle(track.title)}
                    <button onClick={() => playFromIndex(recommendedList, idx, 'REC')} style={{ ...miniBtnStyle, backgroundColor: '#333' }}>▶️ 듣기</button>
                    <button onClick={() => addToPlaylist(track)} style={{ ...miniBtnStyle, backgroundColor: '#1DB954', marginLeft: '5px' }}>➕ 담기</button>
                  </li>
                ))}
              </ol>
            )}
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <div style={{ flex: 1, backgroundColor: '#181818', padding: '20px', borderRadius: '10px' }}>
            <h2 style={{ borderBottom: '1px solid #333', paddingBottom: '10px' }}>💾 나의 플레이리스트 ({currentPlaylist?.tracks.length || 0}곡)</h2>
            <div style={{ marginBottom: "15px" }}>
              <button onClick={createPlaylist} style={playButtonStyle}>➕ 새 플레이리스트</button>
              <select
                value={selectedPlaylistId}
                onChange={(e) => setSelectedPlaylistId(e.target.value)}
                style={{ marginLeft: "10px", padding: "10px" }}
              >
                <option value="">플레이리스트 선택</option>
                {playlists.map(pl => (
                  <option key={pl.id} value={pl.id}>{pl.name}</option>
                ))}
              </select>
              <button onClick={renamePlaylist} disabled={!selectedPlaylistId} style={{ marginLeft: "10px", padding: "10px 15px", borderRadius: "8px", cursor: "pointer" }}>✏️ 이름 변경</button>
              <button onClick={deletePlaylist} disabled={!selectedPlaylistId} style={{ marginLeft: "5px", padding: "10px 15px", borderRadius: "8px", cursor: "pointer" }}>🗑️ 삭제</button>
            </div>
            <ol style={{ paddingLeft: '20px', minHeight: '200px' }}>
              {currentPlaylist?.tracks.map((track, idx) => {
                const safeGenre = track.genre || "Unknown";
                return (
                  <li key={idx} style={{ margin: '15px 0' }}>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      {playingIndex === idx && <span style={{ marginRight: '5px' }}>🎵</span>}
                      <div style={{ flex: 1 }}>{renderTrackTitle(track.title)}</div>
                      <button onClick={() => playFromIndex(currentPlaylist?.tracks || [], idx, 'MY')} style={{padding: '4px 8px',borderRadius: '6px',border: 'none',backgroundColor: '#1DB954',color: 'white',cursor: 'pointer',marginRight: '10px'}}>▶️</button>
                      <span style={{fontSize: '0.75rem', color: getGenreColor(safeGenre), border: `1px solid ${getGenreColor(safeGenre)}`, padding: '2px 6px', borderRadius: '10px', whiteSpace: 'nowrap'}}>{safeGenre}</span>
                    </div>
                  </li>
                );
              })}
            </ol>
            <div style={{ marginTop: '20px' }}>
              <button onClick={() => playFromIndex(currentPlaylist?.tracks || [], 0, 'MY')} style={playButtonStyle}>🚀 리스트 전체 재생</button>
              <button onClick={clearPlaylist} style={{ ...controlButtonStyle, marginLeft: '10px', backgroundColor: '#552222' }}>🗑️ 현재 곡 삭제</button>
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <div style={{ padding: '20px', backgroundColor: '#181818', borderRadius: '10px' }}>
            <h3 style={{ color: '#1DB954', marginBottom: '15px' }}>🔥 벅스 실시간 TOP 100</h3>
            {isChartLoading ? (
              <p style={{ color: '#888' }}>차트 데이터를 긁어오는 중입니다...</p>
            ) : (
              <div style={{ maxHeight: '400px', overflowY: 'auto', backgroundColor: '#121212', borderRadius: '8px', padding: '10px' }}>
                {chartList.map((track, idx) => (
                  <div key={idx} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '10px', borderBottom: '1px solid #282828' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                      <span style={{ color: '#1DB954', fontWeight: 'bold', width: '25px' }}>{track.rank}</span>
                      <div style={{ display: 'flex', flexDirection: 'column' }}>
                        <span style={{ color: '#fff', fontSize: '14px', fontWeight: 'bold' }}>{track.title}</span>
                        <span style={{ color: '#b3b3b3', fontSize: '12px' }}>{track.artist}</span>
                      </div>
                    </div>
                    <button
                      onClick={() => addChartTrackToPlaylist(track)}
                      style={{ backgroundColor: 'transparent', border: '1px solid #1DB954', color: '#1DB954', padding: '5px 10px', borderRadius: '15px', cursor: 'pointer', fontSize: '12px' }}
                      onMouseOver={(e) => { e.currentTarget.style.backgroundColor = '#1DB954'; e.currentTarget.style.color = '#fff'; }}
                      onMouseOut={(e) => { e.currentTarget.style.backgroundColor = 'transparent'; e.currentTarget.style.color = '#1DB954'; }}
                    >
                      + 담기
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <div style={{ flex: 1, backgroundColor: '#181818', padding: '20px', borderRadius: '10px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <h2 style={{ borderBottom: '1px solid #333', paddingBottom: '10px', width: '100%', textAlign: 'center' }}>📊 나의 청취 취향</h2>
            {totalListens === 0 ? (
              <p style={{ color: '#888', marginTop: '50px' }}>음악을 재생하면 분석이 시작됩니다.</p>
            ) : (
              <>
                <div style={{ width: '150px', height: '150px', borderRadius: '50%', background: `conic-gradient(${pieGradients})`, marginTop: '20px', marginBottom: '20px', boxShadow: '0 4px 15px rgba(0,0,0,0.5)' }}></div>
                <div style={{ width: '100%' }}>
                  {Object.entries(genreCounts).sort((a, b) => b[1] - a[1]).map(([genre, count]) => (
                    <div key={genre} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', fontSize: '0.85rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center' }}>
                        <span style={{ display: 'inline-block', width: '10px', height: '10px', backgroundColor: getGenreColor(genre), borderRadius: '50%', marginRight: '8px' }}></span>
                        {genre}
                      </div>
                      <span style={{ color: '#aaa' }}>{((count / totalListens) * 100).toFixed(1)}% ({count}회)</span>
                    </div>
                  ))}
                </div>
                <button onClick={clearHistory} style={{ marginTop: '15px', fontSize: '0.8rem', padding: '6px 12px' }}>초기화</button>
              </>
            )}
          </div>
        </div>

      </div>

    </div>
  );
}