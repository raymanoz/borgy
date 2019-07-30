import React, {Component} from "react";
import {Scale, Scales, Trials} from "./Scale";
import {JsonDecoder} from "ts.data.json";
import {History} from 'history';
import {NavLink} from "react-router-dom";
import Button from 'react-bootstrap/Button';
import {server} from "../utils/server";


interface StartTrialResponse {
    url: string;
}

const responseDecoder = JsonDecoder.object<StartTrialResponse>(
    {
        url: JsonDecoder.string
    },
    'StartTrialResponse'
);

export class Home extends Component<{ history: History }, { scales: Scales, trials: Trials, trialName: String, trialScale: String }> {
    constructor(props: Readonly<{ history: History }>) {
        super(props);
        this.state = {
            scales: [],
            trials: [],
            trialName: "",
            trialScale: ""
        };
        this.renderScaleRadio = this.renderScaleRadio.bind(this);
        this.renderActiveTrial = this.renderActiveTrial.bind(this);
        this.startTrialJsonFromState = this.startTrialJsonFromState.bind(this);
        this.startTrial = this.startTrial.bind(this);
        this.completeTrial = this.completeTrial.bind(this)
    }

    componentDidMount() {
        fetch(server.scales)
            .then(result => result.json())
            .then(s => this.setState({scales: s}));

        this.refreshTrials();
    }

    render() {
        return <div className="container-fluid">
            <h1>Borgy</h1>
            <span id="newTrial">
                <h2>New trial</h2>
                <fieldset>
                    <legend>Start a new trial</legend>
                    <label htmlFor="trial-name">Name</label>
                    <input id="trial-name" type="text"
                           onChange={event => this.onTrialNameChanged(event.target.value)}/><br/>
                    <label>Scale</label><br/>
                    {this.state ? this.state.scales.map(this.renderScaleRadio) : <span>Loading scales</span>}
                    <Button onClick={this.startTrial}>Begin</Button>
                </fieldset>
            </span>
            <br/>
            <span id="activeTrials">
                <h2>Active Trials</h2>
                <ul>
                    {this.state ? this.state.trials.map(this.renderActiveTrial) : <span>Loading active trials</span>}
                </ul>
            </span>
        </div>
    }

    private refreshTrials() {
        fetch(server.trials)
            .then(result => result.json())
            .then(t => this.setState({trials: t}))
    }

    private startTrial() {
        fetch(server.trials, {method: "POST", body: this.startTrialJsonFromState()})
        // TODO : Handle failure response
            .then(result => result.json())
            .then(json => responseDecoder
                .decodePromise(json)
                .then(value => this.props.history.push(value.url))
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
            <input id={radioId} type="radio" name="scale" value={scaleName}
                   onChange={event => this.onTrialScaleChanged(event.target.value)}/>
            <label htmlFor={radioId}>{scaleName}</label><br/>
        </span>;
    }

    private renderActiveTrial(trial: string, idx: number) {
        return <li key={idx}>
            <NavLink to={"/trial/" + trial}>{trial}</NavLink><button onClick={_ => this.completeTrial(trial)}>Complete</button>
        </li>
    }

    private completeTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: 'same-origin'})
            .then(_ => this.refreshTrials())
    }

}