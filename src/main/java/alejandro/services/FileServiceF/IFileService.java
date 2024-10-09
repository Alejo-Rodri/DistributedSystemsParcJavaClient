package alejandro.services.FileServiceF;

import java.io.IOException;
import java.util.List;

import alejandro.model.FileU;

public interface IFileService {
     public List<FileU> getSharedFiles() throws IOException;
}
