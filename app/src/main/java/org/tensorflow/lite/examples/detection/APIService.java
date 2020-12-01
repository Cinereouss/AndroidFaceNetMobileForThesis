package org.tensorflow.lite.examples.detection;

import org.tensorflow.lite.examples.detection.response.CheckConnectionResponse;
import org.tensorflow.lite.examples.detection.response.CheckInResponse;
import org.tensorflow.lite.examples.detection.response.CheckOutResponse;
import org.tensorflow.lite.examples.detection.response.ClassroomResponse;
import org.tensorflow.lite.examples.detection.response.LoginResponse;
import org.tensorflow.lite.examples.detection.response.RegistrationResponse;
import org.tensorflow.lite.examples.detection.response.SearchResponse;
import org.tensorflow.lite.examples.detection.response.StudentEmbeddingResponse;
import org.tensorflow.lite.examples.detection.response.StudentResponse;
import org.tensorflow.lite.examples.detection.response.TeacherEmbeddingResponse;
import org.tensorflow.lite.examples.detection.response.UpdateLocationResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {
    // ***Authentication, authorization API***
    // For account
    @FormUrlEncoded
    @POST("/api/v1/users/login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // For face login
    @FormUrlEncoded
    @POST("api/v1/users/login-by-face")
    Call<LoginResponse> loginByFace(
            @Field("userId") String userId
    );

    // ***Register new face API***
    // For student
    @FormUrlEncoded
    @PATCH("/api/v1/student/register-face")
    Call<RegistrationResponse> registerForStudent(
            @Field("id") String id,
            @Field("embeddings") String embeddings
    );

    // For teacher
    @FormUrlEncoded
    @PATCH("/api/v1/teacher/register-face")
    Call<RegistrationResponse> registerForTeacher(
            @Field("id") String id,
            @Field("embeddings") String embeddings
    );

    // ***Get face data***
    // For teacher
    @GET("/api/v1/teacher?getEmbedding=true")
    Call<TeacherEmbeddingResponse> getTeacherEmbeddingsData();

    // For student by class
    @GET("/api/v1/class/{id}/student?getEmbedding=true")
    Call<StudentEmbeddingResponse> getStudentEmbeddingsData(
            @Path("id") String classId
    );

    // ***Check-in, check-out API***
    // For check in
    @Multipart
    @POST("/api/v1/attendance")
    Call<CheckInResponse> doCheckIn(
            @Part("idHocVien") RequestBody idHocVien,
            @Part MultipartBody.Part imageCheckIn,
            @Part("checkInAt") RequestBody checkInAt,
            @Header("Authorization") String auth);

    // For check out
    @Multipart
    @PATCH("/api/v1/attendance")
    Call<CheckOutResponse> doCheckOut(
            @Part("id") RequestBody id,
            @Part("checkOutAt") RequestBody checkOutAt,
            @Part MultipartBody.Part imageCheckOut,
            @Header("Authorization") String auth);

    // ***Get classes by teacher***
    @GET("/api/v1/teacher/{id}/class")
    Call<ClassroomResponse> getClassesByTeacher(
            @Path("id") String teacherId
    );

    // ***Get students by class***
    @GET("/api/v1/class/{id}/student?getEmbedding=false")
    Call<StudentResponse> getStudentsByClass(
            @Path("id") String classId
    );

    // ***Get student searched data***
    @GET("/api/v1/student/stats/{identity}")
    Call<SearchResponse> getSearchStudentData(
            @Path("identity") String identity
    );

    // ***Update student's location***
    @FormUrlEncoded
    @PATCH("/api/v1/student/student-location/{id}")
    Call<UpdateLocationResponse> updateStudentLocation(
            @Path("id") String id,
            @Field("location") String location
    );

    // ***Check connection to server***
    @GET("/api/v1/attendance/seeding-fake-data")
    Call<CheckConnectionResponse> checkConnection();
}
