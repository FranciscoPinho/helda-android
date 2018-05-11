package com.organon.helda.core.usecases.existsdisassembly;

import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ResponseMessage;

public class ExistDisassemblyResponseMessage extends ResponseMessage<ExistDisassemblyResponseMessage> {
    public Disassembly disassembly;
}
