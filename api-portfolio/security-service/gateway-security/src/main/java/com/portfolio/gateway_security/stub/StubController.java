package com.portfolio.gateway_security.stub;

import com.portfolio.gateway_security.representation.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/v1/stub")
@AllArgsConstructor
public class StubController {
    private final StubResponseService subService;

    @RequestMapping("/{service}/{path}/**" )
    public Mono<ResponseEntity<ResponseDto<?>>> getStubs(@PathVariable String service,
                                                         @PathVariable(required = false) String path,
                                                         ServerWebExchange context){
        return subService.getStubResponse(service, path, context.getRequest().getMethod().name().toLowerCase());
    }

   

}
