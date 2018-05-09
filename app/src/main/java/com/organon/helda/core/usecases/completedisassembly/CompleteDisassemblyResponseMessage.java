package com.organon.helda.core.usecases.completedisassembly;

import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ResponseMessage;


public class CompleteDisassemblyResponseMessage extends ResponseMessage<CompleteDisassemblyResponseMessage> {
    public Disassembly disassembly;
}
