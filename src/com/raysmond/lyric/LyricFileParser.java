package com.raysmond.lyric;

import java.util.List;

public interface LyricFileParser {
	
	public List<LyricLine> parseLyric(String fileName);
	
}
