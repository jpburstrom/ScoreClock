+ Function {
    scoreClockRender {
        arg outputFilePath, duration = 999, padding=0, sampleRate = 44100,
			headerFormat, sampleFormat = "int24", inputFilePath,
			prependScore, args, options, action;

        var tmpfile, score, tempo, sc, clock = TempoClock.default; //Temporary clock

        tempo = clock.tempo;
        sc = ScoreClock(tempo);
        ScoreClock.beginScore;
        ScoreClock.addSynthDefs;
        TempoClock.default = sc;

        this.valueArgsArray(args);


        TempoClock.default = clock;
        score = ScoreClock.makeScore(duration, padding);


        if ( outputFilePath.isNil ) {
            headerFormat = "aiff";
            outputFilePath = thisProcess.platform.recordingsDir +/+ "SC_" ++ Date.localtime.stamp ++ "." ++ headerFormat.toLower;
        };

        if (headerFormat.isNil) {
            //TODO: more header formats than aif/wav?
            if (outputFilePath.splitext[1].toLower == "wav") {
                headerFormat = "wav"
            } {
                headerFormat = "aiff"
            }
        };

        if( prependScore.notNil )
			{ score.score = prependScore ++ score.score };

        tmpfile = PathName.tmp +/+ "tmp_scoreclock" ++ UniqueID.next;

        score.recordNRT(
            tmpfile,
            outputFilePath, inputFilePath, sampleRate, headerFormat, sampleFormat, options,

            duration: ScoreClock.score.score.last[0], action: {
                action.value;
                File.delete(tmpfile);
            }
        );

        //Reset tempo to original
        clock.tempo = tempo;


    }
}

+ Pattern {
    scoreClockRender {
        arg outputFilePath, duration = 999, padding=0, sampleRate = 44100,
			headerFormat, sampleFormat = "int24", inputFilePath,
			prependScore, args, options, action;

        this.stop;

        { this.play }.scoreClockRender(
            outputFilePath, duration, padding, sampleRate, headerFormat,
            sampleFormat, inputFilePath, prependScore, args, options, action
        )
    }
}
