package filip.bedwars.api.requests;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IAdminAPIRequest {
	void parse(DataInput in) throws IOException;
	void process(DataOutput out) throws IOException;
}
