import React, {Component} from "react";
import {Scale, Scales} from "./Scale";
import {JsonDecoder} from "ts.data.json";
import {History} from 'history';

interface StartTrialResponse {
    url: string;
}

const responseDecoder = JsonDecoder.object<StartTrialResponse>(
    {
        url: JsonDecoder.string
    },
    'StartTrialResponse'
);

interface Props {
    history: History
}

export class Home extends Component<Props, { scales: Scales, trialName: String, trialScale: String }> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.state = {
            scales: [],
            trialName: "",
            trialScale: ""
        };
        this.renderScaleRadio = this.renderScaleRadio.bind(this);
        this.startTrialJsonFromState = this.startTrialJsonFromState.bind(this);
        this.startTrial = this.startTrial.bind(this);
    }

    componentDidMount() {
        fetch(`http://localhost:9000/scales`)
            .then(result => result.json())
            .then(s => this.setState({scales: s}))
    }

    render() {
        return <span>
            <h1>Borgy</h1>
            <span id={"newTrial"}>
                <fieldset>
                    <legend>Start a new trial</legend>
                    <label htmlFor="trial-name">Name</label><input id="trial-name" type="text" onChange={event => this.onTrialNameChanged(event.target.value)}/><br/>
                    <label>Scale</label><br/>
                    {this.state ? this.state.scales.map(this.renderScaleRadio) : <span>Loading scales</span>}
                    <button onClick={this.startTrial}>Begin</button>
                </fieldset>
            </span>
        </span>
    }

    private startTrial() {
        fetch(`http://localhost:9000/trial`, {method: "POST", body: this.startTrialJsonFromState()})
        // TODO : Handle failure response
            .then( result => result.json())
            .then( json => responseDecoder
                .decodePromise(json)
                .then(value => this.props.history.push(value.url) )
            )

    }

    private startTrialJsonFromState() {
        return `{ "name": "${this.state.trialName}", "scale": "${this.state.trialScale}" }`;
    }

    private onTrialNameChanged(value: String) {
        this.setState({trialName: value})
    }

    private onTrialScaleChanged(value: String) {
        this.setState({trialScale: value})
    }

    private renderScaleRadio(scale: Scale) {
        let scaleName = `${scale.name}`;
        let radioId = `scale-${scale.name}`;
        return <span key={scaleName}>
            <input id={radioId} type="radio" name="scale" value={scaleName} onChange={event => this.onTrialScaleChanged(event.target.value)}/>
            <label htmlFor={radioId}>{scaleName}</label><br/>
        </span>;
    }
}