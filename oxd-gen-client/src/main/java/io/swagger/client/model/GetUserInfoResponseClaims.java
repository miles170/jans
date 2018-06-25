package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GetUserInfoResponseClaims
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-06-25T15:27:32.160Z")
public class GetUserInfoResponseClaims {
  @SerializedName("sub")
  private List<String> sub = new ArrayList<String>();

  @SerializedName("zoneinfo")
  private List<String> zoneinfo = new ArrayList<String>();

  @SerializedName("website")
  private List<String> website = new ArrayList<String>();

  @SerializedName("birthdate")
  private List<String> birthdate = new ArrayList<String>();

  @SerializedName("gender")
  private List<String> gender = new ArrayList<String>();

  @SerializedName("profile")
  private List<String> profile = new ArrayList<String>();

  @SerializedName("preferred_username")
  private List<String> preferredUsername = new ArrayList<String>();

  @SerializedName("middle_name")
  private List<String> middleName = new ArrayList<String>();

  @SerializedName("locale")
  private List<String> locale = new ArrayList<String>();

  @SerializedName("given_name")
  private List<String> givenName = new ArrayList<String>();

  @SerializedName("picture")
  private List<String> picture = new ArrayList<String>();

  @SerializedName("updated_at")
  private List<String> updatedAt = new ArrayList<String>();

  @SerializedName("nickname")
  private List<String> nickname = new ArrayList<String>();

  @SerializedName("name")
  private List<String> name = new ArrayList<String>();

  @SerializedName("family_name")
  private List<String> familyName = new ArrayList<String>();

  public GetUserInfoResponseClaims sub(List<String> sub) {
    this.sub = sub;
    return this;
  }

  public GetUserInfoResponseClaims addSubItem(String subItem) {
    this.sub.add(subItem);
    return this;
  }

   /**
   * Get sub
   * @return sub
  **/
  @ApiModelProperty(example = "\"jblack\"", required = true, value = "")
  public List<String> getSub() {
    return sub;
  }

  public void setSub(List<String> sub) {
    this.sub = sub;
  }

  public GetUserInfoResponseClaims zoneinfo(List<String> zoneinfo) {
    this.zoneinfo = zoneinfo;
    return this;
  }

  public GetUserInfoResponseClaims addZoneinfoItem(String zoneinfoItem) {
    this.zoneinfo.add(zoneinfoItem);
    return this;
  }

   /**
   * Get zoneinfo
   * @return zoneinfo
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getZoneinfo() {
    return zoneinfo;
  }

  public void setZoneinfo(List<String> zoneinfo) {
    this.zoneinfo = zoneinfo;
  }

  public GetUserInfoResponseClaims website(List<String> website) {
    this.website = website;
    return this;
  }

  public GetUserInfoResponseClaims addWebsiteItem(String websiteItem) {
    this.website.add(websiteItem);
    return this;
  }

   /**
   * Get website
   * @return website
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getWebsite() {
    return website;
  }

  public void setWebsite(List<String> website) {
    this.website = website;
  }

  public GetUserInfoResponseClaims birthdate(List<String> birthdate) {
    this.birthdate = birthdate;
    return this;
  }

  public GetUserInfoResponseClaims addBirthdateItem(String birthdateItem) {
    this.birthdate.add(birthdateItem);
    return this;
  }

   /**
   * Get birthdate
   * @return birthdate
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(List<String> birthdate) {
    this.birthdate = birthdate;
  }

  public GetUserInfoResponseClaims gender(List<String> gender) {
    this.gender = gender;
    return this;
  }

  public GetUserInfoResponseClaims addGenderItem(String genderItem) {
    this.gender.add(genderItem);
    return this;
  }

   /**
   * Get gender
   * @return gender
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getGender() {
    return gender;
  }

  public void setGender(List<String> gender) {
    this.gender = gender;
  }

  public GetUserInfoResponseClaims profile(List<String> profile) {
    this.profile = profile;
    return this;
  }

  public GetUserInfoResponseClaims addProfileItem(String profileItem) {
    this.profile.add(profileItem);
    return this;
  }

   /**
   * Get profile
   * @return profile
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getProfile() {
    return profile;
  }

  public void setProfile(List<String> profile) {
    this.profile = profile;
  }

  public GetUserInfoResponseClaims preferredUsername(List<String> preferredUsername) {
    this.preferredUsername = preferredUsername;
    return this;
  }

  public GetUserInfoResponseClaims addPreferredUsernameItem(String preferredUsernameItem) {
    this.preferredUsername.add(preferredUsernameItem);
    return this;
  }

   /**
   * Get preferredUsername
   * @return preferredUsername
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(List<String> preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  public GetUserInfoResponseClaims middleName(List<String> middleName) {
    this.middleName = middleName;
    return this;
  }

  public GetUserInfoResponseClaims addMiddleNameItem(String middleNameItem) {
    this.middleName.add(middleNameItem);
    return this;
  }

   /**
   * Get middleName
   * @return middleName
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getMiddleName() {
    return middleName;
  }

  public void setMiddleName(List<String> middleName) {
    this.middleName = middleName;
  }

  public GetUserInfoResponseClaims locale(List<String> locale) {
    this.locale = locale;
    return this;
  }

  public GetUserInfoResponseClaims addLocaleItem(String localeItem) {
    this.locale.add(localeItem);
    return this;
  }

   /**
   * Get locale
   * @return locale
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getLocale() {
    return locale;
  }

  public void setLocale(List<String> locale) {
    this.locale = locale;
  }

  public GetUserInfoResponseClaims givenName(List<String> givenName) {
    this.givenName = givenName;
    return this;
  }

  public GetUserInfoResponseClaims addGivenNameItem(String givenNameItem) {
    this.givenName.add(givenNameItem);
    return this;
  }

   /**
   * Get givenName
   * @return givenName
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getGivenName() {
    return givenName;
  }

  public void setGivenName(List<String> givenName) {
    this.givenName = givenName;
  }

  public GetUserInfoResponseClaims picture(List<String> picture) {
    this.picture = picture;
    return this;
  }

  public GetUserInfoResponseClaims addPictureItem(String pictureItem) {
    this.picture.add(pictureItem);
    return this;
  }

   /**
   * Get picture
   * @return picture
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getPicture() {
    return picture;
  }

  public void setPicture(List<String> picture) {
    this.picture = picture;
  }

  public GetUserInfoResponseClaims updatedAt(List<String> updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public GetUserInfoResponseClaims addUpdatedAtItem(String updatedAtItem) {
    this.updatedAt.add(updatedAtItem);
    return this;
  }

   /**
   * Get updatedAt
   * @return updatedAt
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(List<String> updatedAt) {
    this.updatedAt = updatedAt;
  }

  public GetUserInfoResponseClaims nickname(List<String> nickname) {
    this.nickname = nickname;
    return this;
  }

  public GetUserInfoResponseClaims addNicknameItem(String nicknameItem) {
    this.nickname.add(nicknameItem);
    return this;
  }

   /**
   * Get nickname
   * @return nickname
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getNickname() {
    return nickname;
  }

  public void setNickname(List<String> nickname) {
    this.nickname = nickname;
  }

  public GetUserInfoResponseClaims name(List<String> name) {
    this.name = name;
    return this;
  }

  public GetUserInfoResponseClaims addNameItem(String nameItem) {
    this.name.add(nameItem);
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getName() {
    return name;
  }

  public void setName(List<String> name) {
    this.name = name;
  }

  public GetUserInfoResponseClaims familyName(List<String> familyName) {
    this.familyName = familyName;
    return this;
  }

  public GetUserInfoResponseClaims addFamilyNameItem(String familyNameItem) {
    this.familyName.add(familyNameItem);
    return this;
  }

   /**
   * Get familyName
   * @return familyName
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getFamilyName() {
    return familyName;
  }

  public void setFamilyName(List<String> familyName) {
    this.familyName = familyName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetUserInfoResponseClaims getUserInfoResponseClaims = (GetUserInfoResponseClaims) o;
    return Objects.equals(this.sub, getUserInfoResponseClaims.sub) &&
        Objects.equals(this.zoneinfo, getUserInfoResponseClaims.zoneinfo) &&
        Objects.equals(this.website, getUserInfoResponseClaims.website) &&
        Objects.equals(this.birthdate, getUserInfoResponseClaims.birthdate) &&
        Objects.equals(this.gender, getUserInfoResponseClaims.gender) &&
        Objects.equals(this.profile, getUserInfoResponseClaims.profile) &&
        Objects.equals(this.preferredUsername, getUserInfoResponseClaims.preferredUsername) &&
        Objects.equals(this.middleName, getUserInfoResponseClaims.middleName) &&
        Objects.equals(this.locale, getUserInfoResponseClaims.locale) &&
        Objects.equals(this.givenName, getUserInfoResponseClaims.givenName) &&
        Objects.equals(this.picture, getUserInfoResponseClaims.picture) &&
        Objects.equals(this.updatedAt, getUserInfoResponseClaims.updatedAt) &&
        Objects.equals(this.nickname, getUserInfoResponseClaims.nickname) &&
        Objects.equals(this.name, getUserInfoResponseClaims.name) &&
        Objects.equals(this.familyName, getUserInfoResponseClaims.familyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sub, zoneinfo, website, birthdate, gender, profile, preferredUsername, middleName, locale, givenName, picture, updatedAt, nickname, name, familyName);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetUserInfoResponseClaims {\n");
    
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
    sb.append("    zoneinfo: ").append(toIndentedString(zoneinfo)).append("\n");
    sb.append("    website: ").append(toIndentedString(website)).append("\n");
    sb.append("    birthdate: ").append(toIndentedString(birthdate)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    preferredUsername: ").append(toIndentedString(preferredUsername)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

