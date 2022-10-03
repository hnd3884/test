package org.apache.tika.pipes.pipesiterator.filelist;

import org.apache.tika.exception.TikaConfigException;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.config.InitializableProblemHandler;
import java.util.concurrent.TimeoutException;
import java.io.IOException;
import java.io.BufferedReader;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;
import org.apache.tika.utils.StringUtils;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.pipes.pipesiterator.PipesIterator;

public class FileListPipesIterator extends PipesIterator implements Initializable
{
    @Field
    private String fileList;
    @Field
    private boolean hasHeader;
    private Path fileListPath;
    
    public FileListPipesIterator() {
        this.hasHeader = false;
    }
    
    @Override
    protected void enqueue() throws IOException, TimeoutException, InterruptedException {
        try (final BufferedReader reader = Files.newBufferedReader(this.fileListPath, StandardCharsets.UTF_8)) {
            if (this.hasHeader) {
                reader.readLine();
            }
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (!line.startsWith("#") && !StringUtils.isBlank(line)) {
                    final FetchKey fetchKey = new FetchKey(this.getFetcherName(), line);
                    final EmitKey emitKey = new EmitKey(this.getEmitterName(), line);
                    this.tryToAdd(new FetchEmitTuple(line, fetchKey, emitKey, new Metadata(), this.getHandlerConfig(), this.getOnParseException()));
                }
            }
        }
    }
    
    @Field
    public void setFileList(final String path) {
        this.fileList = path;
    }
    
    @Field
    public void setHasHeader(final boolean hasHeader) {
        this.hasHeader = hasHeader;
    }
    
    @Override
    public void checkInitialization(final InitializableProblemHandler problemHandler) throws TikaConfigException {
        TikaConfig.mustNotBeEmpty("fileList", this.fileList);
        TikaConfig.mustNotBeEmpty("fetcherName", this.getFetcherName());
        TikaConfig.mustNotBeEmpty("emitterName", this.getFetcherName());
        this.fileListPath = Paths.get(this.fileList, new String[0]);
        if (!Files.isRegularFile(this.fileListPath, new LinkOption[0])) {
            throw new TikaConfigException("file list " + this.fileList + " does not exist. Must specify an existing file");
        }
    }
}
