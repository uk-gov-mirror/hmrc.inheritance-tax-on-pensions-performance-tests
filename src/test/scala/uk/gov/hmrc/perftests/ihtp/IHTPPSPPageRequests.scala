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
import io.gatling.http.request.builder.HttpRequestBuilder

object IHTPPSPPageRequests extends BaseRequest {

  def getClearData: HttpRequestBuilder =
    http("Clear Data")
      .get(s"$baseUrl/$route/test-only/clear-all": String)
      .check(status.is(200))

  def getLoginToIHTPPageForPsp: HttpRequestBuilder =
    http("Navigate to auth login stub page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postLoginToIHTPPageForPsp: HttpRequestBuilder =
    http("Login to Psp IHTP via Auth Stub")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("redirectionUrl", _ => s"$baseUrl$WhatWillYouNeed")
      .formParam("affinityGroup", _ => "Organisation")
      .formParam("credentialStrength", _ => "strong")
      .formParam("confidenceLevel", _ => "50")
      .formParam("nino", _ => "")
      .formParam("authorityId", _ => "")
      .formParam("enrolment[0].name", _ => "HMRC-PODSPP-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", _ => "PspID")
      .formParam("enrolment[0].taxIdentifier[0].value", _ => "21000005")
      .formParam("enrolment[0].state", _ => "Activated")
      .check(status.is(303))

  def getReportInheritanceTaxOnPensionPageForPsp: HttpRequestBuilder =
    http("Get Report Inheritance Tax on a pension Page")
      .get(s"$baseUrl$route/report-inheritance-tax-on-pension")
      .check(status.is(200))

  def getViewPaidReportsPageForPsp: HttpRequestBuilder =
    http("Get View Paid Inheritance Tax on a pension reports Page")
      .get(s"$baseUrl$route/paid-reports")
      .check(status.is(200))

  def getYouWillNeedPageForPsp: HttpRequestBuilder =
    http("Get What you will need Page")
      .get(s"$baseUrl$WhatWillYouNeed")
      .check(status.is(200))
      .check(bodyString.saveAs("pageBody"))
      .check(saveCsrfToken())

  def postYouWillNeedPageForPsp: HttpRequestBuilder =
    http("Post What you will need Page")
      .post(s"$baseUrl$route/start-report-you-will-need": String)
      .formParam("csrfToken", csrfTokenExpr)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/enter-inheritance-tax-reference": String))

  def getEnterInheritanceTaxReferencePageForPsp: HttpRequestBuilder =
    http("Navigate to Enter the Inheritance Tax reference number Page")
      .get(s"$baseUrl$route/enter-inheritance-tax-reference": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterInheritanceTaxReferencePageForPsp(taxReference: String): HttpRequestBuilder =
    http("Post Enter the Inheritance Tax reference number Page")
      .post(s"$baseUrl$route/enter-inheritance-tax-reference": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", _ => taxReference)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/name-of-deceased": String))

  def getNameOfDeceasedPageForPsp: HttpRequestBuilder =
    http("Navigate to Enter the full name of the deceased person Page")
      .get(s"$baseUrl$route/name-of-deceased": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postNameOfDeceasedPageForPsp(firstForename: String, surname: String): HttpRequestBuilder =
    http("Post Enter the full name of the deceased person Page")
      .post(s"$baseUrl$route/name-of-deceased": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/deceased-has-ni-number": String))

//
//  def getEnterNationalInsuranceNumberPageForPsp: HttpRequestBuilder =
//    http("Navigate to Deceased has a National Insurance number Page")
//      .get(s"$baseUrl$route/enter-national-insurance-number": String)
//      .check(status.is(200))
//      .check(saveCsrfToken())
//
//  def postEnterNationalInsuranceNumberPageForPsp(nino: String): HttpRequestBuilder =
//    http("Post Deceased has a National Insurance number Page")
//      .post(s"$baseUrl$route/enter-national-insurance-number")
//      .formParam("csrfToken", csrfTokenExpr)
//      .formParam("value", _ => "yes")
//      .formParam("nino", _ => nino)
//      .check(status.is(303))
//      .check(header(locationHeaderExpr).is(s"$route/enter-birth-death-date"))

  def getEnterBirthDeathDatePageForPsp: HttpRequestBuilder =
    http("Navigate to Enter DOB and DOD of Deceased Page")
      .get(s"$baseUrl$route/enter-birth-death-date": String)
      .check(status.is(200))
  // .check(saveCsrfToken())

  def postEnterBirthDeathDatePageForPsp: HttpRequestBuilder =
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

  def getPrIndividualOrOrganisationPageForPsp: HttpRequestBuilder =
    http("Navigate to Is the personal representative (PR) an individual or a member of an organisation Page")
      .get(s"$baseUrl$route/pr-individual-or-organisation": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrIndividualOrOrganisationPageForPsp: HttpRequestBuilder =
    http("Post Is the personal representative (PR) an individual or a member of an organisation Page")
      .post(s"$baseUrl$route/pr-individual-or-organisation": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", _ => "organisation")
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/enter-organisation-name": String)
      )

  def getPrOrganisationNamePageForPsp: HttpRequestBuilder =
    http("Navigate to Enter the name of the organisation Page")
      .get(s"$baseUrl$route/enter-organisation-name": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrOrganisationNamePageForPsp(organisationName: String): HttpRequestBuilder =
    http("Post Enter the name of the organisation Page")
      .post(s"$baseUrl$route/enter-organisation-name": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", organisationName: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/enter-name-pr-organisation": String)
      )

  def getNamePrOrganisationPageForPsp: HttpRequestBuilder =
    http("Navigate to Enter the full name of the PR at <Organisation name> Page")
      .get(s"$baseUrl$route/enter-name-pr-organisation": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postNamePrOrganisationPageForPsp(firstForename: String, surname: String): HttpRequestBuilder =
    http("Post Enter the full name of the PR at <Organisation name> Page")
      .post(s"$baseUrl$route/enter-name-pr-organisation": String)
      .disableFollowRedirect
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/enter-pr-organisation-address": String))

  def getPrSubmitPaymentNoticePageForPsp: HttpRequestBuilder =
    http("Navigate to Did PR submit the payment notice? Page")
      .get(s"$baseUrl$route/pr-submit-payment-notice": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postPrSubmitPaymentNoticePageForPsp(submitOption: String): HttpRequestBuilder =
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

  def getSchemeReceivePaymentNoticePageForPsp: HttpRequestBuilder =
    http(
      "Navigate to When did Open Single Trust Scheme with Indiv Establisher and Trustees receive the payment notice? Page"
    )
      .get(s"$baseUrl$route/scheme-receive-payment-notice": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postSchemeReceivePaymentNoticePageForPsp: HttpRequestBuilder =
    http("Post When did Open Single Trust Scheme with Indiv Establisher and Trustees receive the payment notice? Page")
      .post(s"$baseUrl$route/scheme-receive-payment-notice": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("dateThePensionSchemeReceivedNoticeToPay.day", "09": Expression[String])
      .formParam("dateThePensionSchemeReceivedNoticeToPay.month", "11": Expression[String])
      .formParam("dateThePensionSchemeReceivedNoticeToPay.year", "2024": Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/select-beneficiary-type/0": String)
      )

  def getSelectBeneficiaryTypePageForPsp: HttpRequestBuilder =
    http("Navigate to Select the type of beneficiary to add Page")
      .get(s"$baseUrl$route/select-beneficiary-type/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postSelectBeneficiaryTypePageForPsp(submitOption: String): HttpRequestBuilder =
    http("Post Select the type of beneficiary to add Page")
      .post(s"$baseUrl$route/select-beneficiary-type/0": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", submitOption: Expression[String])
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(
          submitOption match {
            case "individual"   => s"$route/enter-name-of-beneficiary/0"
            case "organisation" => s"$route/check-your-answers"
          }
        )
      )

  def getEnterNameOfBeneficiaryForPsp: HttpRequestBuilder =
    http("Navigate to Enter the full name of the beneficiary Page")
      .get(s"$baseUrl$route/enter-name-of-beneficiary/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postEnterNameOfBeneficiaryForPsp(firstForename: String, surname: String): HttpRequestBuilder =
    http("Post Enter the full name of the beneficiary Page")
      .post(s"$baseUrl$route/enter-name-of-beneficiary/0": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("firstForename", _ => firstForename)
      .formParam("surname", _ => surname)
      .check(status.is(303))
      .check(header(locationHeaderExpr).is(s"$route/beneficiary-national-insurance-number/0": String))

  def getBeneficiaryNationalInsuranceNumberPageForPsp: HttpRequestBuilder =
    http("Navigate to Does Joe Doe have a National Insurance number? Page")
      .get(s"$baseUrl$route/beneficiary-national-insurance-number/0": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postBeneficiaryNationalInsuranceNumberPageForPsp(submitOption: String): HttpRequestBuilder =
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

  def getYouAddedABeneficiaryPageForPsp: HttpRequestBuilder =
    http("Navigate to You have added 1 beneficiary Page")
      .get(s"$baseUrl$route/add-beneficiary": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postYouAddedABeneficiaryPageForPsp(submitOption: String): HttpRequestBuilder =
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

  def getAreBeneficiariesKnownPageForPsp: HttpRequestBuilder =
    http("Navigate to Did PR submit the payment notice? Page")
      .get(s"$baseUrl$route/are-beneficiaries-known": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postAreBeneficiariesKnownPageForPsp(submitOption: String): HttpRequestBuilder =
    http("Post Did PR submit the payment notice? Page")
      .post(s"$baseUrl$route/are-beneficiaries-known": String)
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

  def getCYAPageForPsp: HttpRequestBuilder =
    http("Navigate to Check and submit the report Page")
      .get(s"$baseUrl$route/check-your-answers": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postCYAPageForPsp: HttpRequestBuilder =
    http("Post Check and submit the reportPage")
      .post(s"$baseUrl$route/check-your-answers": String)
      .formParam("csrfToken", csrfTokenExpr)
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/pspDeclaration": String)
      )

  def getDeclarationPageForPsp: HttpRequestBuilder =
    http("Navigate to PSP Declaration Page")
      .get(s"$baseUrl$route/pspDeclaration": String)
      .check(status.is(200))
      .check(saveCsrfToken())

  def postDeclarationPageForPsp: HttpRequestBuilder =
    http("Enter Administrator's ID on PSP Declaration Page")
      .post(s"$baseUrl$route/pspDeclaration": String)
      .formParam("csrfToken", csrfTokenExpr)
      .formParam("value", _ => "A2100005")
      .check(status.is(303))
      .check(
        header(locationHeaderExpr).is(s"$route/ihtp-report-submitted": String)
      )

  def getIHTPReportSubmittedPageForPsp: HttpRequestBuilder =
    http("Navigate to Report submitted Page")
      .get(s"$baseUrl$route/ihtp-report-submitted": String)
      .check(status.is(200))

}
