/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.ihtp

import io.gatling.core.Predef.*
import io.gatling.core.session.Expression
import io.gatling.http.Predef.*
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder

object IHTPPageRequests extends BaseRequest {

  def getClearData: HttpRequestBuilder =
    http("Clear Data")
      .get(s"$baseUrl/$route/test-only/clear-all": String)
      .check(status.is(200))

  def getLoginToIHTPPage: HttpRequestBuilder =
    http("Navigate to auth login stub page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postLoginToIHTPPage: HttpRequestBuilder =
    http("Login to Psa IHTP via Auth Stub")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("redirectionUrl", _ => s"$baseUrl$route/report-inheritance-tax-on-pension")
      .formParam("affinityGroup", _ => "Organisation")
      .formParam("credentialStrength", _ => "strong")
      .formParam("confidenceLevel", _ => "50")
      .formParam("nino", _ => "")
      .formParam("authorityId", _ => "")
      .formParam("enrolment[0].name", _ => "HMRC-PODS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", _ => "PsaID")
      .formParam("enrolment[0].taxIdentifier[0].value", _ => "A2100005")
      .formParam("enrolment[0].state", _ => "Activated")
      .check(status.is(303))

  def getReportInheritanceTaxOnPensionPage: HttpRequestBuilder =
    http("Get Report Inheritance Tax on a pension Page")
      .get(s"$baseUrl$route/report-inheritance-tax-on-pension")
      .check(status.is(200))

  def getViewPaidReportsPage: HttpRequestBuilder =
    http("Get View Paid Inheritance Tax on a pension reports Page")
      .get(s"$baseUrl$route/paid-reports")
      .check(status.is(200))

  def getYouWillNeedPage: HttpRequestBuilder =
    http("Get What you will need Page")
      .get(s"$baseUrl$WhatWillYouNeed")
      .check(status.is(200))
      .check(bodyString.saveAs("pageBody"))
      .check(saveCsrfToken())

  def postYouWillNeedPage: HttpRequestBuilder =
    http("Post What you will need Page")
      .post(s"$baseUrl$route/start-report-you-will-need": String)
      .formParam("csrfToken", csrfTokenExpr)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/enter-inheritance-tax-reference": String))

  def getEnterInheritanceTaxReferencePage: HttpRequestBuilder =
    http("Navigate to Enter the Inheritance Tax reference number Page")
      .get(s"$baseUrl$route/enter-inheritance-tax-reference": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterInheritanceTaxReferencePage(taxReference: String): HttpRequestBuilder =
    http("Post Enter the Inheritance Tax reference number Page")
      .post(s"$baseUrl$route/enter-inheritance-tax-reference": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", _ => taxReference)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/name-of-deceased": String))

  def getNameOfDeceasedPage: HttpRequestBuilder =
    http("Navigate to Enter the full name of the deceased person Page")
      .get(s"$baseUrl$route/name-of-deceased": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postNameOfDeceasedPage(firstForename: String, surname: String): HttpRequestBuilder =
    http("Post Enter the full name of the deceased person Page")
      .post(s"$baseUrl$route/name-of-deceased": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/deceased-has-ni-number": String))

  def getDoesDeceasedHasNationalInsuranceNumberPage: HttpRequestBuilder =
    http("Navigate to Does Deceased has a National Insurance number Page")
      .get(s"$baseUrl$route/deceased-has-ni-number": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postDoesDeceasedHasNationalInsuranceNumberPage(deceasednino: String): HttpRequestBuilder =
    http("Post Deceased has a National Insurance number Page")
      .post(s"$baseUrl$route/deceased-has-ni-number")
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", deceasednino: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          deceasednino match {
            case "true"  => s"$route/enter-ni-number"
            case "false" => s"$route/reason-no-ni-number"
          }
        )
      )

  def getEnterNationalInsuranceNumberPage: HttpRequestBuilder =
    http("Navigate to Enter National Insurance number of Deceased Page")
      .get(s"$baseUrl$route/enter-ni-number": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterNationalInsuranceNumberPage(): HttpRequestBuilder =
    http("Post Enter National Insurance number of Deceased Page")
      .post(s"$baseUrl$route/enter-ni-number")
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", _ => nino)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/enter-birth-death-date"))

  def getEnterBirthDeathDatePage: HttpRequestBuilder =
    http("Navigate to Enter DOB and DOD of Deceased Page")
      .get(s"$baseUrl$route/enter-birth-death-date": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterBirthDeathDatePage: HttpRequestBuilder =
    http("Post Enter DOB and DOD of Deceased Page")
      .post(s"$baseUrl$route/enter-birth-death-date": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("dateOfBirth.day", "09": Expression[String])
      .formParam("dateOfBirth.month", "11": Expression[String])
      .formParam("dateOfBirth.year", "1990": Expression[String])
      .formParam("dateOfDeath.day", "09": Expression[String])
      .formParam("dateOfDeath.month", "11": Expression[String])
      .formParam("dateOfDeath.year", "2023": Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/pr-individual-or-organisation": String)
      )

  def getPrIndividualOrOrganisationPage: HttpRequestBuilder =
    http("Navigate to Is the personal representative (PR) an individual or a member of an organisation Page")
      .get(s"$baseUrl$route/pr-individual-or-organisation": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrIndividualOrOrganisationPage(lprType: String): HttpRequestBuilder =
    http("Post Is the personal representative (PR) an individual or a member of an organisation Page")
      .post(s"$baseUrl$route/pr-individual-or-organisation": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", lprType: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          lprType match {
            case "individual"   => s"$route/enter-name-pr"
            case "organisation" => s"$route/enter-organisation-name"
          }
        )
      )

  def getPrIndividualNamePage: HttpRequestBuilder =
    http("Navigate to Enter the name of the Individual Page")
      .get(s"$baseUrl$route/enter-name-pr": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrIndividualNamePage(firstForename: String, surname: String): HttpRequestBuilder =
    http("Enter the full name of the person managing the estate Page")
      .post(s"$baseUrl$route/enter-name-pr": String)
      .disableFollowRedirect
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/enter-pr-address": String))

  def getPrOrganisationNamePage: HttpRequestBuilder =
    http("Navigate to Enter the name of the organisation Page")
      .get(s"$baseUrl$route/enter-organisation-name": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrOrganisationNamePage(organisationName: String): HttpRequestBuilder =
    http("Post Enter the name of the organisation Page")
      .post(s"$baseUrl$route/enter-organisation-name": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", organisationName: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/enter-name-pr-organisation": String)
      )

  def getNamePrOrganisationPage: HttpRequestBuilder =
    http("Navigate to Enter the full name of the PR at <Organisation name> Page")
      .get(s"$baseUrl$route/enter-name-pr-organisation": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postNamePrOrganisationPage(firstForename: String, surname: String): HttpRequestBuilder =
    http("Post Enter the full name of the PR at <Organisation name> Page")
      .post(s"$baseUrl$route/enter-name-pr-organisation": String)
      .disableFollowRedirect
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/enter-pr-organisation-address": String))

  def getPrSubmitPaymentNoticePage: HttpRequestBuilder =
    http("Navigate to Did PR submit the payment notice? Page")
      .get(s"$baseUrl$route/pr-submit-payment-notice": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrSubmitPaymentNoticePage(submitOption: String): HttpRequestBuilder =
    http("Post Did PR submit the payment notice? Page")
      .post(s"$baseUrl$route/pr-submit-payment-notice": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", submitOption: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          submitOption match {
            case "true"  => s"$route/scheme-receive-payment-notice"
            case "false" => s"$route/scheme-receive-payment-notice"
          }
        )
      )

  def getSchemeReceivePaymentNoticePage: HttpRequestBuilder =
    http(
      "Navigate to When did Open Single Trust Scheme with Indiv Establisher and Trustees receive the payment notice? Page"
    )
      .get(s"$baseUrl$route/scheme-receive-payment-notice": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postSchemeReceivePaymentNoticePage: HttpRequestBuilder =
    http("Post When did Open Single Trust Scheme with Indiv Establisher and Trustees receive the payment notice? Page")
      .post(s"$baseUrl$route/scheme-receive-payment-notice": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("dateThePensionSchemeReceivedNoticeToPay.day", "09": Expression[String])
      .formParam("dateThePensionSchemeReceivedNoticeToPay.month", "11": Expression[String])
      .formParam("dateThePensionSchemeReceivedNoticeToPay.year", "2024": Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/are-beneficiaries-known": String)
      )

  def getAreBeneficiariesKnownPage: HttpRequestBuilder =
    http("Navigate to Did PR submit the payment notice? Page")
      .get(s"$baseUrl$route/are-beneficiaries-known": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postAreBeneficiariesKnownPage(submitOption: String): HttpRequestBuilder =
    http("Post Did PR submit the payment notice? Page")
      .post(s"$baseUrl$route/are-beneficiaries-known": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", submitOption: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          submitOption match {
            case "true"  => s"$route/select-beneficiary-type/0"
            case "false" => s"$route/check-your-answers"
          }
        )
      )

  def getSelectBeneficiaryTypePage: HttpRequestBuilder =
    http("Navigate to Select the type of beneficiary to add Page")
      .get(s"$baseUrl$route/select-beneficiary-type/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postSelectBeneficiaryTypePage(submitOption: String): HttpRequestBuilder =
    http("Post Select the type of beneficiary to add Page")
      .post(s"$baseUrl$route/select-beneficiary-type/0": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", submitOption: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          submitOption match {
            case "individual" => s"$route/enter-name-of-beneficiary/0"
            case "trust"      => s"$route/beneficiary-organisation-details/0"
          }
        )
      )

  def getEnterNameOfBeneficiary: HttpRequestBuilder =
    http("Navigate to Enter the full name of the beneficiary Page")
      .get(s"$baseUrl$route/enter-name-of-beneficiary/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterNameOfBeneficiary(firstForename: String, surname: String): HttpRequestBuilder =
    http("Post Enter the full name of the beneficiary Page")
      .post(s"$baseUrl$route/enter-name-of-beneficiary/0": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/beneficiary-national-insurance-number/0": String))

  def getEnterNameOfTrust: HttpRequestBuilder =
    http("Navigate to Enter the name of the trust Page")
      .get(s"$baseUrl$route/beneficiary-organisation-details/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterNameOfTrust(trustName: String): HttpRequestBuilder =
    http("Post Enter the name of the trust Page")
      .post(s"$baseUrl$route/beneficiary-organisation-details/0": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", trustName: Expression[String])
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/add-beneficiary": String))

  def getBeneficiaryNationalInsuranceNumberPage: HttpRequestBuilder =
    http("Navigate to Does Joe Doe have a National Insurance number? Page")
      .get(s"$baseUrl$route/beneficiary-national-insurance-number/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postBeneficiaryNationalInsuranceNumberPage(submitOption: String): HttpRequestBuilder =
    http("Post Does Joe Doe have a National Insurance number? Page")
      .post(s"$baseUrl$route/beneficiary-national-insurance-number/0": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", submitOption: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          submitOption match {
            case "true"  => s"$route/add-beneficiary"
            case "false" => s"$route/add-beneficiary"
          }
        )
      )

  def getYouAddedABeneficiaryPage: HttpRequestBuilder =
    http("Navigate to You have added 1 beneficiary Page")
      .get(s"$baseUrl$route/add-beneficiary": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postYouAddedABeneficiaryPage(submitOption: String): HttpRequestBuilder =
    http("Post You have added 1 beneficiary Page")
      .post(s"$baseUrl$route/add-beneficiary": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", submitOption: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          submitOption match {
            case "true"  => s"$route/check-your-answers"
            case "false" => s"$route/check-your-answers"
          }
        )
      )

  def getCYAPage: HttpRequestBuilder =
    http("Navigate to Check and submit the report Page")
      .get(s"$baseUrl$route/check-your-answers": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postCYAPage: HttpRequestBuilder =
    http("Post Check and submit the reportPage")
      .post(s"$baseUrl$route/check-your-answers": String)
      .formParam("csrfToken", csrfTokenExpr)
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/psa-declaration": String)
      )

  def getPsaDeclarationPage: HttpRequestBuilder =
    http("Navigate to Declaration Page")
      .get(s"$baseUrl$route/psa-declaration": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPsaDeclarationPage: HttpRequestBuilder =
    http("Post Declaration Page")
      .post(s"$baseUrl$route/psa-declaration": String)
      .formParam("csrfToken", csrfTokenExpr)
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/ihtp-report-submitted": String)
      )

  def getPsaIHTPReportSubmittedPage: HttpRequestBuilder =
    http("Navigate to Report submitted Page")
      .get(s"$baseUrl$route/ihtp-report-submitted": String)
      .check(status.is(200))

}
