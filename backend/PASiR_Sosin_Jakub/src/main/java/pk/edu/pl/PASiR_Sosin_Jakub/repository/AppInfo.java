package pk.edu.pl.PASiR_Sosin_Jakub.repository;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppInfo {
    public String appName;
    public String appVersion;
    public String message;
}
