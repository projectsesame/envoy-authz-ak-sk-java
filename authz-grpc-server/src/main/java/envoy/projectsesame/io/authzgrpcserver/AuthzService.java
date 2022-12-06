package envoy.projectsesame.io.authzgrpcserver;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Map;
import java.util.TreeMap;


/**
 * @author yangyang
 * @date 2022/12/6 下午4:45
 */
@GrpcService()
public class AuthzService extends AuthorizationGrpc.AuthorizationImplBase {

    @Override
    public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
        CheckResponse checkResponse = null;
        // 查找是否有auth头
        Map<String, String> headersMap = request.getAttributes().getRequest().getHttp().getHeadersMap();
        if (headersMap.containsKey("authorization") && headersMap.containsKey("x-date")){
            String authorization = headersMap.get("authorization");
            String date = headersMap.get("x-date");
            new String(request.getAttributes().getRequest().getHttp().getRawBody().toByteArray());
            // 校验请求的时间为15分钟之内
            if (!CheckSign.checkDate(Long.parseLong(date))){
                checkResponse = CheckResponse.newBuilder().setDeniedResponse(DeniedHttpResponse.newBuilder().setBody("Access token expired \n")).setStatus(Status.newBuilder().setCode(Code.PERMISSION_DENIED_VALUE)).build();
                responseObserver.onNext(checkResponse);
                responseObserver.onCompleted();
                return;
            }
            String path = request.getAttributes().getRequest().getHttp().getPath();

            try {
               SignInfo signInfo = CheckSign.checkSignFormat(authorization);
                // 获取签名的header
                String[] headers = signInfo.getSignHeaders();
                String signHeader = CheckSign.getSignHeader(headersMap, headers);
                String signQuery = CheckSign.getSignQuery(request.getAttributes().getRequest().getHttp().getPath());
                String signPath = CheckSign.getSignPath(path);
                String body = CheckSign.getSignBody(request.getAttributes().getRequest().getHttp());


                RequestInfo requestInfo = RequestInfo.builder()
                        .method(request.getAttributes().getRequest().getHttp().getMethod())
                        .path(signPath)
                        .headers(signHeader)
                        .params(signQuery)
                        .body(body)
                        .date(date)
                        .build();

                // 校验签名是否一致
                if (requestInfo.checkSign(signInfo)){
                    checkResponse = CheckResponse.newBuilder().setOkResponse(OkHttpResponse.getDefaultInstance()).setStatus(Status.newBuilder().setCode(Code.OK_VALUE)).build();
                } else {
                    checkResponse = CheckResponse.newBuilder().setDeniedResponse(DeniedHttpResponse.newBuilder().setBody("Sign error\n")).setStatus(Status.newBuilder().setCode(Code.PERMISSION_DENIED_VALUE).build()).build();
                }
                responseObserver.onNext(checkResponse);
                responseObserver.onCompleted();

            }catch (Exception e){
                checkResponse = CheckResponse.newBuilder().setDeniedResponse(DeniedHttpResponse.newBuilder().setBody("No permission\n")).setStatus(Status.newBuilder().setCode(Code.PERMISSION_DENIED_VALUE).build()).build();
                responseObserver.onNext(checkResponse);
                responseObserver.onCompleted();
            }

        }
        checkResponse = CheckResponse.newBuilder().setDeniedResponse(DeniedHttpResponse.newBuilder().setBody("No permission\n")).setStatus(Status.newBuilder().setCode(Code.PERMISSION_DENIED_VALUE).build()).build();
        responseObserver.onNext(checkResponse);
        responseObserver.onCompleted();

        System.out.println("check");
    }
}
