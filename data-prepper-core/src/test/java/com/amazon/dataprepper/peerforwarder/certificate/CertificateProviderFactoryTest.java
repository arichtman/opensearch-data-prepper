/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.amazon.dataprepper.peerforwarder.certificate;

import com.amazon.dataprepper.peerforwarder.PeerForwarderConfiguration;
import com.amazon.dataprepper.plugins.certificate.CertificateProvider;
import com.amazon.dataprepper.plugins.certificate.acm.ACMCertificateProvider;
import com.amazon.dataprepper.plugins.certificate.file.FileCertificateProvider;
import com.amazon.dataprepper.plugins.certificate.s3.S3CertificateProvider;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateProviderFactoryTest {
    private PeerForwarderConfiguration peerForwarderConfiguration;
    private CertificateProviderFactory certificateProviderFactory;

    @BeforeEach
    void setUp() {
        peerForwarderConfiguration = mock(PeerForwarderConfiguration.class);
    }

    @Test
    void getCertificateProviderAcmProviderSuccess() {
        when(peerForwarderConfiguration.isUseAcmCertificateForSsl()).thenReturn(true);
        when(peerForwarderConfiguration.getAwsRegion()).thenReturn("us-east-1");
        when(peerForwarderConfiguration.getAcmCertificateArn()).thenReturn("arn:aws:acm:us-east-1:account:certificate/1234-567-856456");

        certificateProviderFactory = new CertificateProviderFactory(peerForwarderConfiguration);
        final CertificateProvider certificateProvider = certificateProviderFactory.getCertificateProvider();

        assertThat(certificateProvider, IsInstanceOf.instanceOf(ACMCertificateProvider.class));
    }

    @Test
    void getCertificateProviderS3ProviderSuccess() {
        when(peerForwarderConfiguration.isSslCertAndKeyFileInS3()).thenReturn(true);
        when(peerForwarderConfiguration.getAwsRegion()).thenReturn("us-east-1");
        when(peerForwarderConfiguration.getSslCertificateFile()).thenReturn("s3://data/certificate/test_cert.crt");
        when(peerForwarderConfiguration.getSslKeyFile()).thenReturn("s3://data/certificate/test_decrypted_key.key");

        certificateProviderFactory = new CertificateProviderFactory(peerForwarderConfiguration);
        final CertificateProvider certificateProvider = certificateProviderFactory.getCertificateProvider();

        assertThat(certificateProvider, IsInstanceOf.instanceOf(S3CertificateProvider.class));
    }

    @Test
    void getCertificateProviderFileProviderSuccess() {
        when(peerForwarderConfiguration.getSslCertificateFile()).thenReturn("data/certificate/test_cert.crt");
        when(peerForwarderConfiguration.getSslKeyFile()).thenReturn("data/certificate/test_decrypted_key.key");

        certificateProviderFactory = new CertificateProviderFactory(peerForwarderConfiguration);
        final CertificateProvider certificateProvider = certificateProviderFactory.getCertificateProvider();

        assertThat(certificateProvider, IsInstanceOf.instanceOf(FileCertificateProvider.class));
    }
}