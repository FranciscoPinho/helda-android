package com.organon.helda.core.usecases.createdisassembly;

import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ResponseMessage;

public class CreateDisassemblyResponseMessage extends ResponseMessage<CreateDisassemblyResponseMessage> {
    public Disassembly disassembly;
}
