package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
public class AppInfo {
    public final String appName;
    public final String appVersion;
    public final String message;
}
